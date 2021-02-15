package io.github.zero88.qwe.gradle.docker

import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty

@Suppress("UnstableApiUsage") class QWEDockerExtension(objects: ObjectFactory, projectLayout: ProjectLayout) {

    val enabled = objects.property<Boolean>().convention(true)

    val appDir = objects.property<String>().convention("/app")
    val dataDir = objects.property<String>().convention("/data")
    val user = objects.property<String>().convention("qwe")
    val userId = objects.property<Int>().convention(804)
    val group = objects.property<String>().convention("root")
    val groupId = objects.property<Int>().convention(0)
    val ports = objects.setProperty<Int>().convention(setOf(8080, 5000))
    val configFile = objects.property<String>().convention("config.json")

    val dockerRegistries = objects.setProperty<String>().convention(setOf("docker.io"))
    val dockerTags = objects.setProperty<String>()
    val dockerLabels = objects.setProperty<String>()
}
