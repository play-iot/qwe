package io.github.zero88.qwe.gradle.app.task

import org.gradle.api.tasks.TaskAction

open class ConfigGeneratorTask : QWEGeneratorTask("Generates application configuration") {

    @TaskAction
    override fun generate() {
        project.copy {
            into(outputDir.get())
            from("src/main/resources") {
                include("*.json")
            }
        }
    }
}
