package io.github.zero88.qwe.gradle.generator

import io.github.zero88.qwe.gradle.QWEPlugin
import io.github.zero88.qwe.gradle.generator.task.ConfigGeneratorTask
import io.github.zero88.qwe.gradle.generator.task.LoggingGeneratorTask
import io.github.zero88.qwe.gradle.generator.task.UnixServiceGeneratorTask
import io.github.zero88.qwe.gradle.helper.prop
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources

class QWEGeneratorPlugin : QWEPlugin() {

    override fun doApply(project: Project, rootExtName: String) {
        val rootExt = project.extensions.getByName<ExtensionAware>(rootExtName)
        val generator = rootExt.extensions.create<QWEGeneratorExtension>("generator")
        val configProvider = project.tasks.register<ConfigGeneratorTask>("generateConfig") {
            outputDir.set(generator.layout.generatedConfigDir)
        }
        val loggingProvider = project.tasks.register<LoggingGeneratorTask>("generateLogging") {
            loggers.set(generator.logging.specified)
            outputDir.set(generator.layout.generatedConfigDir)
            onlyIf { "true" == prop(project, "executable") }
        }
        val unixProvider = project.tasks.register<UnixServiceGeneratorTask>("generateUnixService") {
            outputDir.set(generator.layout.generatedConfigDir)
            onlyIf { "true" == prop(project, "executable") }
        }
        project.tasks {
            withType<ProcessResources>()
                .configureEach { dependsOn(configProvider.get(), loggingProvider.get(), unixProvider.get()) }

            named<AbstractArchiveTask>("distZip") {
                onlyIf { "true" == prop(project, "executable") }
                into("${prop(project, "baseName")}-${project.version}/conf") {
                    from(generator.layout.generatedConfigDir.get())
                }
            }
            named<AbstractArchiveTask>("distTar") {
                onlyIf { "true" == prop(project, "executable") }
                into("${prop(project, "baseName")}-${project.version}/conf") {
                    from(generator.layout.generatedConfigDir.get())
                }
            }
        }

    }

}
