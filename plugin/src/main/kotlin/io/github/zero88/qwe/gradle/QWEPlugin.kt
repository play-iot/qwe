package io.github.zero88.qwe.gradle

import io.github.zero88.qwe.gradle.helper.computeBaseName
import io.github.zero88.qwe.gradle.helper.prop
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.distribution.DistributionContainer
import org.gradle.api.distribution.plugins.DistributionPlugin
import org.gradle.api.plugins.JavaLibraryDistributionPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.testing.Test
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.internal.jvm.Jvm
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.gradle.util.GradleVersion
import java.net.URI
import java.time.Instant
import java.util.jar.Attributes

@Suppress("UnstableApiUsage")
abstract class QWEPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val qwe = project.extensions.create<QWEExtension>("qwe")
        qwe.baseName.convention(computeBaseName(project))
        qwe.title.convention(prop(project, "title", qwe.baseName.get()))
        qwe.description.convention(prop(project, "description"))
        qwe.application.convention(prop(project, "executable", "false").toBoolean())
        qwe.publishingInfo.projectName.convention(qwe.baseName.get())
        project.extra.set("baseName", qwe.baseName.get())

        applyExternalPlugins(project)
        evaluateProject(project, qwe)
        configExternalTasks(project, qwe)
        doApply(project, qwe)
    }

    abstract fun doApply(project: Project, extension: QWEExtension)

    protected open fun applyExternalPlugins(project: Project) {
        project.pluginManager.apply(JavaLibraryDistributionPlugin::class.java)
        project.pluginManager.apply(MavenPublishPlugin::class.java)
        project.pluginManager.apply(SigningPlugin::class.java)
    }

    protected open fun evaluateProject(project: Project, qwe: QWEExtension) {
        project.afterEvaluate {
            println("- Project Name:     ${qwe.baseName.get()}")
            println("- Project Title:    ${qwe.title.get()}")
            println("- Project Group:    ${project.group}")
            println("- Project Version:  ${project.version}")
            println("- Gradle Version:   ${GradleVersion.current()}")
            println("- Java Version:     ${Jvm.current()}")
            configureExtension(project, qwe)
        }
    }

    protected open fun configureExtension(project: Project, qwe: QWEExtension) {
        project.extensions.configure<JavaPluginExtension> {
            withJavadocJar()
            withSourcesJar()
        }
        project.extensions.getByName<DistributionContainer>("distributions")
            .named(DistributionPlugin.MAIN_DISTRIBUTION_NAME)
            .configure { distributionBaseName.set(qwe.baseName) }
        if (qwe.publishingInfo.enabled.get()) {
            project.extensions.configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("maven") {
                        groupId = project.group as String?
                        artifactId = qwe.baseName.get()
                        version = project.version as String?
                        from(project.components["java"])

                        versionMapping {
                            usage("java-api") {
                                fromResolutionOf("runtimeClasspath")
                            }
                            usage("java-runtime") {
                                fromResolutionResult()
                            }
                        }
                        pom {
                            name.set(qwe.title)
                            description.set(qwe.description)
                            url.set(qwe.publishingInfo.homepage)
                            licenses {
                                license {
                                    name.set(qwe.publishingInfo.license.name)
                                    url.set(qwe.publishingInfo.license.url)
                                    comments.set(qwe.publishingInfo.license.comments)
                                    distribution.set(qwe.publishingInfo.license.distribution)
                                }
                            }
                            developers {
                                developer {
                                    id.set(qwe.publishingInfo.developer.id)
                                    email.set(qwe.publishingInfo.developer.email)
                                    organization.set(qwe.publishingInfo.developer.organization)
                                }
                            }
                            scm {
                                connection.set(qwe.publishingInfo.scm.connection)
                                developerConnection.set(qwe.publishingInfo.scm.developerConnection)
                                url.set(qwe.publishingInfo.scm.url)
                                tag.set(qwe.publishingInfo.scm.tag)
                            }
                        }
                    }
                }
                repositories {
                    maven {
                        val path = if (project.hasProperty("github")) {
                            val ghRepoUrl = prop(project, "github.nexus.url")
                            val ghOwner = prop(project, "nexus.username")
                            "${ghRepoUrl}/${ghOwner}/${qwe.publishingInfo.projectName.get()}"
                        } else {
                            val releasesRepoUrl = prop(project, "ossrh.release.url")
                            val snapshotsRepoUrl = prop(project, "ossrh.snapshot.url")
                            if (project.hasProperty("release")) releasesRepoUrl else snapshotsRepoUrl
                        }
                        url = path?.let { URI(it) }!!
                        credentials {
                            username = prop(project, "nexus.username")
                            password = prop(project, "nexus.password")
                        }
                    }
                }
            }
            project.extensions.configure<SigningExtension> {
                useGpgCmd()
                sign(project.extensions.findByType(PublishingExtension::class.java)?.publications?.get("maven"))
            }
        }
    }

    protected open fun configExternalTasks(project: Project, qwe: QWEExtension) {
        project.tasks {
            withType<Jar>().configureEach {
                archiveBaseName.set(prop(project, "baseName"))
                manifest {
                    attributes(createJarManifest(project, qwe, this@configureEach))
                }
            }
            withType<Sign>().configureEach {
                onlyIf { project.hasProperty("release") }
            }
            withType<Javadoc> {
                title = "${qwe.title.get()} ${project.version} API"
                options {
                    this as StandardJavadocDocletOptions
                    tags = mutableListOf(
                        "apiNote:a:API Note:", "implSpec:a:Implementation Requirements:",
                        "implNote:a:Implementation Note:"
                    )
                }
            }
            withType<Test> {
                useJUnitPlatform()
            }
        }
    }

    protected open fun createJarManifest(project: Project, qwe: QWEExtension, task: Jar): Map<String, Any?> {
        var manifestMap: Map<String, String> = emptyMap()
        if (qwe.application.get()) {
            val mainClass = project.extra.get("mainClass").toString()
            val mainVerticle = project.extra.get("mainVerticle").toString()
            if (mainClass.trim() == "" || mainVerticle.trim() == "") {
                throw TaskExecutionException(task, RuntimeException("Missing mainClass or mainVerticle"))
            }
            val runtime = project.configurations.getByName("runtimeClasspath")
            val classPath = if (runtime.isEmpty) "" else runtime.files.joinToString(" ") { "lib/${it.name}" }
            manifestMap = mapOf(
                "Main-Class" to mainClass,
                "Main-Verticle" to mainVerticle,
                "Class-Path" to "$classPath conf/"
            )
        }
        return mapOf(
            Attributes.Name.MANIFEST_VERSION.toString() to "1.0",
            Attributes.Name.IMPLEMENTATION_TITLE.toString() to prop(project, "baseName"),
            Attributes.Name.IMPLEMENTATION_VERSION.toString() to project.version,
            "Created-By" to GradleVersion.current(),
            "Build-Jdk" to Jvm.current(),
            "Build-By" to project.property("buildBy"),
            "Build-Hash" to project.property("buildHash"),
            "Build-Date" to Instant.now()
        ) + manifestMap
    }

}
