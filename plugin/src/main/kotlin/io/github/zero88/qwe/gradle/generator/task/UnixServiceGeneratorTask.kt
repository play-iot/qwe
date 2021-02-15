package io.github.zero88.qwe.gradle.generator.task

import org.gradle.api.tasks.TaskAction

open class UnixServiceGeneratorTask : QWEGeneratorTask("Generates Systemd Linux service file") {

    @TaskAction
    override fun generate() {
        println("TBC")
    }
}
