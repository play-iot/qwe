package io.github.zero88.qwe.gradle.generator.task

import io.github.zero88.qwe.gradle.helper.prop
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.mapProperty

open class LoggingGeneratorTask : QWEGeneratorTask("Generates logging configuration") {

    @Input
    var loggers: MapProperty<String, String> = project.objects.mapProperty(String::class, String::class)

    @TaskAction
    override fun generate() {
        val pName = prop(project, "baseName", project.name)
        val loggers = loggers.get().map { "<logger name=\"${it.key}\" level=\"${it.value}\"/>" }.joinToString("\r\n")
        val url = "${LoggingGeneratorTask::class.java.classLoader.getResource("logger")}"
        val from: Any = if (url.startsWith("jar:")) project.zipTree(
            url.removePrefix("jar:").removeSuffix("!/logger")
        ) else project.file(url)
        val include = if (url.startsWith("jar:")) "logger/*.xml.template" else "*.xml.template"
        project.copy {
            into(outputDir.get())
            from(from) {
                include(include)
                eachFile {
                    relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
                }
                includeEmptyDirs = false
                rename("((?!console))+(\\.console)?\\.xml\\.template", "$1.xml")
                filter {
                    it.replace("\\{\\{project\\}\\}", pName).replace("\\{\\{more_logger\\}\\}", loggers)
                }
            }
        }
    }
}
