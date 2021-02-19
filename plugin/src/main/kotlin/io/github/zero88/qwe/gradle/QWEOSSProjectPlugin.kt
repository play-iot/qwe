package io.github.zero88.qwe.gradle

import io.github.zero88.qwe.gradle.helper.computeBaseName
import io.github.zero88.qwe.gradle.helper.prop
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.distribution.DistributionContainer
import org.gradle.api.distribution.plugins.DistributionPlugin
import org.gradle.api.plugins.JavaLibraryDistributionPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.testing.Test
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.internal.jvm.Jvm
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.util.GradleVersion
import java.net.URI
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.jar.Attributes

@Suppress("UnstableApiUsage")
class QWEOSSProjectPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        applyExternalPlugins(project)
        val qwe = evaluateProject(project)
        project.tasks {
            configExternalTasks(project, qwe)
        }
    }

    private fun applyExternalPlugins(project: Project) {
        project.pluginManager.apply(JavaLibraryDistributionPlugin::class.java)
        project.pluginManager.apply(JacocoPlugin::class.java)
        project.pluginManager.apply(MavenPublishPlugin::class.java)
        project.pluginManager.apply(SigningPlugin::class.java)
    }

    private fun evaluateProject(project: Project): QWEExtension {
        val qweExt = project.extensions.create<QWEExtension>(QWEExtension.NAME)
        qweExt.baseName.convention(computeBaseName(project))
        qweExt.title.convention(prop(project, "title", qweExt.baseName.get()))
        qweExt.description.convention(prop(project, "description"))
        qweExt.application.convention(prop(project, "executable", "false").toBoolean())
        qweExt.publishingInfo.projectName.convention(qweExt.baseName.get())
        project.extra.set("baseName", qweExt.baseName.get())
        project.version = "${project.version}${prop(project, "semanticVersion")}"
        project.afterEvaluate {
            println("- Project Name:     ${qweExt.baseName.get()}")
            println("- Project Title:    ${qweExt.title.get()}")
            println("- Project Group:    ${project.group}")
            println("- Project Version:  ${project.version}")
            println("- Gradle Version:   ${GradleVersion.current()}")
            println("- Java Version:     ${Jvm.current()}")
            println("- Build Hash:       ${prop(project, "buildHash")}")
            println("- Build By:         ${prop(project, "buildBy")}")
            if (qweExt.zero88.get()) {
                qweExt.publishingInfo.developer {
                    id.set(QWEExtension.DEV_ID)
                    email.set(QWEExtension.DEV_EMAIL)
                }
            }
            configureExtension(project, qweExt)
        }
        return qweExt
    }

    private fun configureExtension(project: Project, qweExt: QWEExtension) {
        project.extensions.configure<JavaPluginExtension> {
            withJavadocJar()
            withSourcesJar()
        }
        project.extensions.getByName<DistributionContainer>("distributions")
            .named(DistributionPlugin.MAIN_DISTRIBUTION_NAME)
            .configure { distributionBaseName.set(qweExt.baseName) }
        if (qweExt.publishingInfo.enabled.get()) {
            val publicationName = "maven"
            project.extensions.configure<PublishingExtension> {
                publications {
                    createMavenPublication(publicationName, project, qweExt)
                }
                repositories {
                    maven {
                        url = computeMavenRepositoryUrl(project, qweExt)
                        credentials {
                            username = prop(project, "nexus.username")
                            password = prop(project, "nexus.password")
                        }
                    }
                }
            }
            project.extensions.configure<SigningExtension> {
                useGpgCmd()
                sign(project.extensions.findByType<PublishingExtension>()?.publications?.get(publicationName))
            }
        }
    }

    private fun PublicationContainer.createMavenPublication(
        publicationName: String,
        project: Project,
        qweExt: QWEExtension
    ) {
        create<MavenPublication>(publicationName) {
            groupId = project.group as String?
            artifactId = qweExt.baseName.get()
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
                name.set(qweExt.title)
                description.set(qweExt.description)
                url.set(qweExt.publishingInfo.homepage)
                licenses {
                    license {
                        name.set(qweExt.publishingInfo.license.name)
                        url.set(qweExt.publishingInfo.license.url)
                        comments.set(qweExt.publishingInfo.license.comments)
                        distribution.set(qweExt.publishingInfo.license.distribution)
                    }
                }
                developers {
                    developer {
                        id.set(qweExt.publishingInfo.developer.id)
                        email.set(qweExt.publishingInfo.developer.email)
                        organization.set(qweExt.publishingInfo.developer.organization)
                    }
                }
                scm {
                    connection.set(qweExt.publishingInfo.scm.connection)
                    developerConnection.set(qweExt.publishingInfo.scm.developerConnection)
                    url.set(qweExt.publishingInfo.scm.url)
                    tag.set(qweExt.publishingInfo.scm.tag)
                }
            }
        }
    }

    private fun TaskContainerScope.configExternalTasks(project: Project, qwe: QWEExtension) {
        withType<JavaCompile>().configureEach {
            options.encoding = StandardCharsets.UTF_8.name()
        }
        named<Jar>(JavaPlugin.JAR_TASK_NAME) {
            manifest {
                attributes(
                    mapOf(
                        Attributes.Name.MANIFEST_VERSION.toString() to "1.0",
                        Attributes.Name.IMPLEMENTATION_TITLE.toString() to qwe.baseName.get(),
                        Attributes.Name.IMPLEMENTATION_VERSION.toString() to project.version,
                        "Created-By" to GradleVersion.current(),
                        "Build-Jdk" to Jvm.current(),
                        "Build-By" to prop(project, "buildBy"),
                        "Build-Hash" to prop(project, "buildHash"),
                        "Build-Date" to Instant.now()
                    ) + qwe.manifest.get()
                )
            }
        }
        withType<Jar>().configureEach {
            archiveBaseName.set(qwe.baseName)
        }
        withType<Sign>().configureEach {
            onlyIf { project.hasProperty("release") }
        }
        withType<Javadoc> {
            title = "${qwe.title.get()} ${project.version} API"
            options {
                encoding = StandardCharsets.UTF_8.name()
                this as StandardJavadocDocletOptions
                tags = mutableListOf(
                    "apiNote:a:API Note:", "implSpec:a:Implementation Requirements:",
                    "implNote:a:Implementation Note:"
                )
            }
        }
        withType<Test> {
            useJUnitPlatform()
            systemProperty("file.encoding", StandardCharsets.UTF_8.name())
        }
    }

    private fun computeMavenRepositoryUrl(project: Project, qwe: QWEExtension): URI {
        val path = if (project.hasProperty("github")) {
            val ghRepoUrl = prop(project, "github.nexus.url")
            val ghOwner = prop(project, "nexus.username")
            "${ghRepoUrl}/${ghOwner}/${qwe.publishingInfo.projectName.get()}"
        } else {
            val releasesRepoUrl = prop(project, "ossrh.release.url")
            val snapshotsRepoUrl = prop(project, "ossrh.snapshot.url")
            if (project.hasProperty("release")) releasesRepoUrl else snapshotsRepoUrl
        }
        return path?.let { URI(it) }!!
    }
}
