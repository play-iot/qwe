package io.github.zero88.qwe.gradle.generator.task

import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.mapProperty

open class LoggingExtension(objects: ObjectFactory) {

    val specified = objects.mapProperty(String::class, String::class)
}

