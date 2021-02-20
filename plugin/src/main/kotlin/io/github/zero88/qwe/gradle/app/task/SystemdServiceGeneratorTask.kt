package io.github.zero88.qwe.gradle.app.task

import io.github.zero88.qwe.gradle.helper.getPluginResource
import io.github.zero88.qwe.gradle.helper.readResourceProperties
import org.gradle.api.file.RelativePath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property

open class SystemdServiceGeneratorTask : QWEGeneratorTask("Generates Systemd Linux service file") {

    @Input
    val baseName = project.objects.property<String>()

    @Input
    val projectDes = project.objects.property<String>()

    @Nested
    val systemdProp = project.objects.property<SystemdServiceExtension>()

    @TaskAction
    override fun generate() {
        val jarFile = baseName.get() + "-" + project.version
        val resource = getPluginResource(project, "service")
        val input = systemdProp.get()
        val props = readResourceProperties("service/java.${input.arch.orNull?.name?.toLowerCase()}.properties")
        val jvmProps = if (input.jvmProps.get().isNotEmpty())
            input.jvmProps.map { it.joinToString { " " } }.get() else props?.getProperty("jvm") ?: ""
        val systemProps = if (input.systemProps.get().isNotEmpty())
            input.systemProps.map { it.joinToString(" ", "-D") }.get() else props?.getProperty("system") ?: ""
        val configParam = input.configFile.map { "-conf $it" }.getOrElse("")
        val params = input.params.map { it.entries.map { kv -> "-${kv.key} ${kv.value}" }.joinToString { " " } }
            .getOrElse("")
        project.copy {
            into(outputDir.get())
            from(resource.first) {
                include("service/systemd.service.template")
                rename { input.serviceName.getOrElse(baseName.get()).plus(".service") }
                eachFile {
                    relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
                }
                includeEmptyDirs = false
                filter {
                    it.replace("{{java_path}}", input.javaPath.get())
                        .replace("{{description}}", projectDes.get())
                        .replace("{{jvm}}", jvmProps)
                        .replace("{{system}}", systemProps)
                        .replace("{{working_dir}}", input.workingDir.get())
                        .replace("{{jar_file}}", jarFile)
                        .replace("{{params}}", configParam + params)
                }
            }
        }
    }
}
