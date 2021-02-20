package io.github.zero88.qwe.gradle.docker.task

import com.bmuschko.gradle.docker.tasks.image.DockerPushImage
import com.github.dockerjava.api.model.AuthConfig
import com.github.dockerjava.core.NameParser
import org.gradle.api.tasks.TaskExecutionException

open class DockerMultipleRegistriesPushTask : DockerPushImage() {

    override fun runRemoteCommand() {
        if (images.get().isEmpty()) {
            throw TaskExecutionException(this, RuntimeException("No images configured for push operation."))
        }
        val imagesByRegistry = images.get().groupBy { getRegistry(it) }.toMap()
        val auths = imagesByRegistry.keys.map { it to lookupAuth(it) }.toMap()
        imagesByRegistry.forEach {
            val auth = auths[it.key]
            logger.quiet("Pushing image '${it.value}' to registry '${it.key}'.")
            it.value.forEach { each -> dockerClient.pushImageCmd(each).withAuthConfig(auth).start().awaitCompletion() }
        }
    }

    private fun getRegistry(image: String): String =
        NameParser.resolveRepositoryName(NameParser.parseRepositoryTag(image).repos).hostname

    fun lookupAuth(reg: String): AuthConfig =
        registryAuthLocator.lookupAuthConfig(if (reg == "docker.io") "mock" else "$reg/mock", registryCredentials)
}
