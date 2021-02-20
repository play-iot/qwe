package io.github.zero88.qwe.gradle.docker.task

import org.gradle.api.Project
import org.gradle.kotlin.dsl.task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class DockerMultipleRegistriesPushTaskTest {

    @Test
    fun test_lookup_auth() {
        val project: Project = ProjectBuilder.builder().build()
        val task = project.task<DockerMultipleRegistriesPushTask>("greeting")
        val lookupAuth = task.lookupAuth("docker.io/abc")
        Assertions.assertEquals("https://index.docker.io/v1/", lookupAuth.registryAddress)
    }
}
