package io.github.zero88.qwe.gradle.generator.task

import io.github.zero88.qwe.gradle.helper.getPluginResource
import io.github.zero88.qwe.gradle.helper.prop
import org.gradle.api.file.RelativePath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.mapProperty

open class LoggingGeneratorTask : QWEGeneratorTask("Generates logging configuration") {

    @Input
    var loggers = project.objects.mapProperty<String, String>()

    @TaskAction
    override fun generate() {
        val pName = prop(project, "baseName", project.name)
        val loggers = loggers.get().map { "<logger name=\"${it.key}\" level=\"${it.value}\"/>" }.joinToString("\r\n")
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
                    it.replace("{{project}}", pName).replace("{{more_logger}}", loggers)
                }
            }
        }
    }
}
