package io.github.zero88.qwe.gradle.docker

import org.gradle.api.Action
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property

@Suppress("UnstableApiUsage") open class QWEDockerExtension(objects: ObjectFactory, layout: ProjectLayout) {

    val enabled = objects.property<Boolean>().convention(true)
    val maintainer = objects.property<String>().convention("")
    val dockerfile = DockerfileExtension(objects)
    val dockerImage = DockerImageExtension(objects)
    val outputDirectory = objects.directoryProperty().convention(layout.buildDirectory.dir("docker"))

    fun dockerfile(configuration: Action<DockerfileExtension>) {
        configuration.execute(dockerfile)
    }

    fun dockerImage(configuration: Action<DockerImageExtension>) {
        configuration.execute(dockerImage)
    }

    companion object {

        const val NAME = "qweApplication"
    }

}
