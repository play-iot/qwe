package io.github.zero88.qwe.gradle

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.api.publish.maven.MavenPomLicense
import org.gradle.api.publish.maven.MavenPomScm
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPomDeveloper
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPomLicense
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPomScm
import org.gradle.kotlin.dsl.property

@Suppress("UnstableApiUsage") class PublishingInfo(objects: ObjectFactory) {

    val enabled = objects.property<Boolean>().convention(false)
    val projectName = objects.property<String>()
    val homepage = objects.property<String>()
    val license: MavenPomLicense = DefaultMavenPomLicense(objects)
    val developer: MavenPomDeveloper = DefaultMavenPomDeveloper(objects)
    val scm: MavenPomScm = DefaultMavenPomScm(objects)

    fun license(configuration: Action<MavenPomLicense>) {
        configuration.execute(license)
    }

    fun developer(configuration: Action<MavenPomDeveloper>) {
        configuration.execute(developer)
    }

    fun scm(configuration: Action<MavenPomScm>) {
        configuration.execute(scm)
    }
}
