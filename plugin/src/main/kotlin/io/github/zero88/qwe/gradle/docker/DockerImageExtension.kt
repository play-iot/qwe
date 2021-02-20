package io.github.zero88.qwe.gradle.docker

import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.setProperty

@Suppress("UnstableApiUsage")
open class DockerImageExtension(objects: ObjectFactory) {

    val imageRegistries = objects.setProperty<String>()
    val tags = objects.setProperty<String>().empty()
    val labels = objects.mapProperty<String, String>().empty()

    fun toFQNImages(): List<String> {
        return imageRegistries.get().flatMap { r -> tags.get().map { v -> "${r}:${v}" } }
    }
}
