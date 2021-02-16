package io.github.zero88.qwe.gradle.docker

import com.bmuschko.gradle.docker.DockerExtension
import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import io.github.zero88.qwe.gradle.QWEExtension
import io.github.zero88.qwe.gradle.generator.QWEGeneratorPlugin
import io.github.zero88.qwe.gradle.helper.prop
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

@Suppress("UnstableApiUsage") class QWEDockerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply(DockerRemoteApiPlugin::class.java)
        project.plugins.apply(QWEGeneratorPlugin::class.java)
        val qweExtension = project.extensions.getByType<QWEExtension>()
        val dockerExtension = project.extensions.getByType<DockerExtension>()
        val qweDockerExt = configureExtension(project, qweExtension, dockerExtension)
        val dockerFileProvider = registerCreateDockerfileTask(project, qweExtension.baseName, qweDockerExt)
        registerPrintDockerfileTask(project, qweDockerExt, dockerFileProvider)
        registerDockerBuildTask(project, qweExtension.baseName, qweDockerExt, dockerFileProvider)
    }

    private fun configureExtension(
        project: Project,
        qweExt: QWEExtension,
        dockerExt: DockerExtension
    ): QWEDockerExtension {
        val ext = (dockerExt as ExtensionAware).extensions.create<QWEDockerExtension>(QWE_DOCKER_EXTENSION_NAME)
        project.afterEvaluate {
            val name = qweExt.baseName.get()
            val registryParams = prop(project, "dockerRegistries", true)?.split(",")?.map { "${it}/${name}" }
            val tagParams = prop(project, "dockerTags")?.split(",")
            val labelParams = prop(project, "dockerLabels", true)?.split(",")?.filter { s -> s.isNotEmpty() }
            val dl = listOf("version=${project.version}", "maintainer=zero88 <sontt246@gmail.com>")
            val labels = dl + (labelParams ?: listOf())
            ext.enabled.set(qweExt.application.get() && ext.enabled.get())
            ext.dockerImage.registries.addAll(registryParams ?: listOf(name))
            ext.dockerImage.tags.addAll(tagParams ?: listOf(project.version.toString()))
            ext.dockerImage.labels.putAll(labels.map { s -> s.split("=") }.map { it[0] to it[1] }.toMap())
        }
        return ext
    }

    private fun registerCreateDockerfileTask(
        project: Project,
        baseName: Property<String>,
        qweDockerExt: QWEDockerExtension
    ): TaskProvider<Dockerfile> {
        return project.tasks.register<Dockerfile>("createDockerfile") {
            group = "QWE Docker"
            description = "Create Dockerfile"
            val fqn = baseName.get() + "-" + project.version
            val df = qweDockerExt.dockerfile

            onlyIf { qweDockerExt.enabled.get() }
            destFile.set(project.layout.buildDirectory.file("docker/${baseName.get()}"))

            from(df.image.get())
            workingDir(df.appDir)
            addFile("distributions/${fqn}.tar", "./")
            runCommand("cp -rf $fqn/* ./ && rm -rf $fqn && mkdir -p ${df.dataDir.get()}")
            runCommand(
                "useradd -u ${df.userId.get()} -G ${df.group.get()} ${df.user.get()} " +
                    "&& chown -R ${df.user.get()}:${df.group.get()} ${df.dataDir.get()} " +
                    "&& chmod -R 755 ${df.dataDir.get()}"
            )
            volume(df.dataDir.get())
            user(df.user.get())
            entryPoint("java")
            defaultCommand("-jar", "${fqn}.jar", "-conf", "conf/${df.configFile.get()}")
            label(qweDockerExt.dockerImage.labels)
            exposePort(df.ports)
        }
    }

    private fun registerPrintDockerfileTask(
        project: Project,
        docker: QWEDockerExtension,
        provider: TaskProvider<Dockerfile>
    ) {
        project.tasks.register<DefaultTask>("printDockerfile") {
            group = "QWE Docker"
            description = "Show Dockerfile"

            onlyIf { docker.enabled.get() }
            doLast {
                val instructions = provider.get().instructions.get()
                println(instructions.joinToString(System.lineSeparator()) { it.text })
            }
        }
    }

    private fun registerDockerBuildTask(
        project: Project,
        baseName: Property<String>,
        qweDockerExt: QWEDockerExtension,
        dockerFileProvider: TaskProvider<Dockerfile>
    ) {
        project.tasks.register<DockerBuildImage>("buildDocker") {
            group = "QWE Docker"
            description = "Build Docker image from createDockerfile task output"

            onlyIf { qweDockerExt.enabled.get() }
            dependsOn(project.tasks.getByName(BasePlugin.ASSEMBLE_TASK_NAME), dockerFileProvider.get())

            inputDir.set(project.layout.buildDirectory)
            dockerFile.set(project.layout.buildDirectory.file("docker/${baseName.get()}"))
            images.value(qweDockerExt.dockerImage.toImages())
        }
    }

    companion object {

        const val QWE_DOCKER_EXTENSION_NAME = "qweApplication"
    }
}
