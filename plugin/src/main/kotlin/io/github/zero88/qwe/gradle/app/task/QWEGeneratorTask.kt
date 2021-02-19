package io.github.zero88.qwe.gradle.app.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@Suppress("UnstableApiUsage")
abstract class QWEGeneratorTask(_description: String) : DefaultTask() {

    @OutputDirectory
    val outputDir: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    abstract fun generate()

    init {
        group = "QWE Generator"
        description = _description
    }
}
