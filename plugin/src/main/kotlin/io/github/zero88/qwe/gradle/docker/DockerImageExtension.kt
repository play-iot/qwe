package io.github.zero88.qwe.gradle.docker

import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.setProperty

@Suppress("UnstableApiUsage")
open class DockerImageExtension(objects: ObjectFactory) {

    val registries = objects.setProperty<String>().convention(setOf("docker.io"))
    val tags = objects.setProperty<String>().convention(emptySet())
    val labels = objects.mapProperty<String, String>().convention(emptyMap())

    fun toImages(): List<String> {
        return registries.get().flatMap { r -> tags.get().map { v -> "${r}:${v}" } }
    }
}
