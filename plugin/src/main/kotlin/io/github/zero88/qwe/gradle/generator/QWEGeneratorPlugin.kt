package io.github.zero88.qwe.gradle.generator

import io.github.zero88.qwe.gradle.QWEExtension
import io.github.zero88.qwe.gradle.QWEPlugin
import io.github.zero88.qwe.gradle.generator.task.ConfigGeneratorTask
import io.github.zero88.qwe.gradle.generator.task.LoggingGeneratorTask
import io.github.zero88.qwe.gradle.generator.task.UnixServiceGeneratorTask
import io.github.zero88.qwe.gradle.helper.prop
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources

@Suppress("UnstableApiUsage")
class QWEGeneratorPlugin : QWEPlugin() {

    override fun doApply(project: Project, extension: QWEExtension) {
        val generator = (extension as ExtensionAware).extensions.create<QWEGeneratorExtension>("generator")
        val configProvider = project.tasks.register<ConfigGeneratorTask>("generateConfig") {
            outputDir.set(generator.layout.generatedConfigDir)
        }
        val loggingProvider = project.tasks.register<LoggingGeneratorTask>("generateLogging") {
            loggers.set(generator.logging.specified)
            outputDir.set(generator.layout.generatedConfigDir)
            onlyIf { extension.application.get() }
        }
        val unixProvider = project.tasks.register<UnixServiceGeneratorTask>("generateUnixService") {
            outputDir.set(generator.layout.generatedConfigDir)
            onlyIf { extension.application.get() }
        }
        project.tasks {
            withType<ProcessResources>()
                .configureEach { dependsOn(configProvider, loggingProvider, unixProvider) }

            named<AbstractArchiveTask>("distZip") {
                onlyIf { extension.application.get() }
                into("${prop(project, "baseName")}-${project.version}/conf") {
                    from(generator.layout.generatedConfigDir.get())
                }
            }
            named<AbstractArchiveTask>("distTar") {
                onlyIf { extension.application.get() }
                into("${prop(project, "baseName")}-${project.version}/conf") {
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
