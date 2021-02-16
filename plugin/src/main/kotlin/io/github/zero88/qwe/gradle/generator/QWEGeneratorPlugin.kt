package io.github.zero88.qwe.gradle.generator

import io.github.zero88.qwe.gradle.QWEExtension
import io.github.zero88.qwe.gradle.QWEProjectPlugin
import io.github.zero88.qwe.gradle.generator.task.ConfigGeneratorTask
import io.github.zero88.qwe.gradle.generator.task.LoggingGeneratorTask
import io.github.zero88.qwe.gradle.generator.task.SystemdServiceGeneratorTask
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources

@Suppress("UnstableApiUsage")
class QWEGeneratorPlugin : QWEProjectPlugin() {

    override fun doApply(project: Project, extension: QWEExtension) {
        val generator = (extension as ExtensionAware).extensions.create<QWEGeneratorExtension>("generator")
        val configProvider = project.tasks.register<ConfigGeneratorTask>("generateConfig") {
            outputDir.set(generator.layout.generatedConfigDir)
        }
        val loggingProvider = project.tasks.register<LoggingGeneratorTask>("generateLogging") {
            onlyIf { extension.application.get() }
            loggers.set(generator.logging.specified)
            outputDir.set(generator.layout.generatedConfigDir)
        }
        val systemdProvider = project.tasks.register<SystemdServiceGeneratorTask>("generateSystemdService") {
            onlyIf { extension.application.get() && generator.systemd.enabled.get() }
            baseName.set(extension.baseName)
            projectDes.set(extension.description.convention(extension.title))
            systemdProp.set(generator.systemd)
            outputDir.set(generator.layout.generatedServiceDir)
        }
        project.tasks {
            withType<ProcessResources>()
                .configureEach { dependsOn(configProvider, loggingProvider, systemdProvider) }

            named<AbstractArchiveTask>("distZip") {
                onlyIf { extension.application.get() }
                into("${extension.baseName.get()}-${project.version}/conf") {
                    from(generator.layout.generatedConfigDir.get())
                }
            }
            named<AbstractArchiveTask>("distTar") {
                onlyIf { extension.application.get() }
                into("${extension.baseName.get()}-${project.version}/conf") {
                    from(generator.layout.generatedConfigDir.get())
                }
            }
        }
        configureSourceSet(project, generator.layout)
    }

    private fun configureSourceSet(project: Project, layout: GeneratedLayoutExtension) {
        val sourceSets = project.convention.getPlugin<JavaPluginConvention>().sourceSets
        sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).java.srcDirs.add(layout.generatedJavaSrcDir.get().asFile)
        sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).resources.srcDirs.add(layout.generatedResourceDir.get().asFile)

        sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME).java.srcDirs.add(layout.generatedJavaSrcTestDir.get().asFile)
        sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME).resources.srcDirs.add(layout.generatedResourceTestDir.get().asFile)
    }

}
