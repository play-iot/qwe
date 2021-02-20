package io.github.zero88.qwe.gradle.app

import io.github.zero88.qwe.gradle.QWEDecoratorPlugin
import io.github.zero88.qwe.gradle.QWEExtension
import io.github.zero88.qwe.gradle.QWEOSSProjectPlugin
import io.github.zero88.qwe.gradle.app.task.ConfigGeneratorTask
import io.github.zero88.qwe.gradle.app.task.LoggingGeneratorTask
import io.github.zero88.qwe.gradle.app.task.SystemdServiceGeneratorTask
import io.github.zero88.qwe.gradle.helper.prop
import org.gradle.api.Project
import org.gradle.api.java.archives.ManifestException
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources

/**
 * This plugin adds Generator/Bundle capabilities to QWE Application
 */
@Suppress("UnstableApiUsage")
class QWEAppPlugin : QWEDecoratorPlugin<QWEAppExtension> {

    override fun applyExternalPlugins(project: Project) {
        project.plugins.apply(QWEOSSProjectPlugin::class.java)
    }

    override fun configureExtension(project: Project, qweExt: QWEExtension): QWEAppExtension {
        project.afterEvaluate {
            if (qweExt.application.get()) {
                val mainClass = prop(project, "mainClass", MAIN_CLASS)
                val mainVerticle = prop(project, "mainVerticle", "")
                if (mainClass.trim() == "" || mainVerticle.trim() == "") {
                    throw ManifestException("Missing Vertx mainClass or mainVerticle")
                }
                val runtime = project.configurations.getByName("runtimeClasspath")
                val classPath = if (runtime.isEmpty) "" else runtime.files.joinToString(" ") { "lib/${it.name}" }
                qweExt.manifest.putAll(
                    mapOf(
                        "Main-Class" to mainClass,
                        "Main-Verticle" to mainVerticle,
                        "Class-Path" to "$classPath conf/"
                    )
                )
            }
        }
        return (qweExt as ExtensionAware).extensions.create(QWEAppExtension.NAME)
    }

    override fun registerAndConfigureTask(project: Project, qweExt: QWEExtension, decoratorExt: QWEAppExtension) {
        val configProvider = project.tasks.register<ConfigGeneratorTask>("generateConfig") {
            outputDir.set(decoratorExt.layout.generatedConfigDir)
        }
        val loggingProvider = project.tasks.register<LoggingGeneratorTask>("generateLogging") {
            onlyIf { qweExt.application.get() }
            projectName.set(qweExt.baseName)
            ext.set(decoratorExt.logging)
            outputDir.set(decoratorExt.layout.generatedConfigDir)
        }
        val systemdProvider = project.tasks.register<SystemdServiceGeneratorTask>("generateSystemdService") {
            onlyIf { qweExt.application.get() && decoratorExt.systemd.enabled.get() }
            baseName.set(qweExt.baseName)
            projectDes.set(qweExt.description.convention(qweExt.title))
            systemdProp.set(decoratorExt.systemd)
            outputDir.set(decoratorExt.layout.generatedServiceDir)
        }
        project.tasks {
            withType<ProcessResources>()
                .configureEach { dependsOn(configProvider, loggingProvider, systemdProvider) }

            named<AbstractArchiveTask>("distZip") {
                onlyIf { qweExt.application.get() }
                into("${qweExt.baseName.get()}-${project.version}/conf") {
                    from(decoratorExt.layout.generatedConfigDir.get())
                }
            }
            named<AbstractArchiveTask>("distTar") {
                onlyIf { qweExt.application.get() }
                into("${qweExt.baseName.get()}-${project.version}/conf") {
                    from(decoratorExt.layout.generatedConfigDir.get())
                }
            }
        }
        configureSourceSet(project, decoratorExt.layout)
    }

    private fun configureSourceSet(project: Project, layout: GeneratedLayoutExtension) {
        val sourceSets = project.convention.getPlugin<JavaPluginConvention>().sourceSets
        sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).java.srcDirs.add(layout.generatedJavaSrcDir.get().asFile)
        sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).resources.srcDirs.add(layout.generatedResourceDir.get().asFile)

        sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME).java.srcDirs.add(layout.generatedJavaSrcTestDir.get().asFile)
        sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME).resources.srcDirs.add(layout.generatedResourceTestDir.get().asFile)
    }

    companion object {

        const val MAIN_CLASS = "io.github.zero88.qwe.CarlLauncher"
    }

}
