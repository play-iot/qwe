package io.github.zero88.qwe.gradle.app.task

import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property

@Suppress("UnstableApiUsage")
open class LoggingExtension(objects: ObjectFactory) {

    @Input
    val rootLogLevel = objects.property<String>().convention("info")

    @Input
    val zeroLibLevel = objects.property<String>().convention("info")

    @Input
    val otherLoggers = objects.mapProperty<String, String>().empty()
}

