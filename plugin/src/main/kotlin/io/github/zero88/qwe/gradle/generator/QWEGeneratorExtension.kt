package io.github.zero88.qwe.gradle.generator

import io.github.zero88.qwe.gradle.generator.task.LoggingExtension
import io.github.zero88.qwe.gradle.generator.task.SystemdServiceExtension
import org.gradle.api.Action
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory

open class QWEGeneratorExtension(objects: ObjectFactory, projectLayout: ProjectLayout) {

    val layout = GeneratedLayoutExtension(objects, projectLayout)
    val logging = LoggingExtension(objects)
    val systemd = SystemdServiceExtension(objects)

    fun layout(configuration: Action<GeneratedLayoutExtension>) {
        configuration.execute(layout)
    }

    fun logging(configuration: Action<LoggingExtension>) {
        configuration.execute(logging)
    }

    fun systemd(configuration: Action<SystemdServiceExtension>) {
        configuration.execute(systemd)
    }
}
