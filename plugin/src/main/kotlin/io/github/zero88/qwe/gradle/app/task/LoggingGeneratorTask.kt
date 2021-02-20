package io.github.zero88.qwe.gradle.app.task

import io.github.zero88.qwe.gradle.helper.getPluginResource
import org.gradle.api.file.RelativePath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property

@Suppress("UnstableApiUsage")
open class LoggingGeneratorTask : QWEGeneratorTask("Generates logging configuration") {

    @Input
    val projectName = project.objects.property<String>().convention(project.name)

    @Nested
    val ext = project.objects.property<LoggingExtension>().convention(LoggingExtension(project.objects))

    @TaskAction
    override fun generate() {
        val loggers = ext.get().otherLoggers.get()
            .map { "<logger name=\"${it.key}\" level=\"${it.value}\"/>" }.joinToString("\r\n")
        val resource = getPluginResource(project, "logger")
        project.copy {
            into(outputDir.get())
            from(resource.first) {
                include(if (resource.second) "logger/*.xml.template" else "*.xml.template")
                eachFile {
                    relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
                }
                includeEmptyDirs = false
                rename("((?!console))+(\\.console)?\\.xml\\.template", "$1.xml")
                filter {
                    it.replace("{{project}}", projectName.get())
                        .replace("{{root_level}}", ext.get().rootLogLevel.get())
                        .replace("{{zero_lib_level}}", ext.get().zeroLibLevel.get())
                        .replace("{{other_logger}}", loggers)
                }
            }
        }
    }
}
