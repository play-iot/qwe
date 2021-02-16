package io.github.zero88.qwe.gradle.docker

import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property

@Suppress("UnstableApiUsage")
open class DockerfileExtension(objects: ObjectFactory) {

    val image = objects.property<String>().convention("openjdk:8-jre-slim")
    val appDir = objects.property<String>().convention("/app")
    val dataDir = objects.property<String>().convention("/data")
    val user = objects.property<String>().convention("qwe")
    val userId = objects.property<Int>().convention(804)
    val group = objects.property<String>().convention("root")
    val groupId = objects.property<Int>().convention(0)
    val ports = objects.listProperty<Int>().convention(listOf(8080, 5000))
    val configFile = objects.property<String>().convention("config.json")
}
