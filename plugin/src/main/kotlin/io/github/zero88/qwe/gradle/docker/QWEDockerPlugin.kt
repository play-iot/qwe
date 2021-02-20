package io.github.zero88.qwe.gradle.docker

import com.bmuschko.gradle.docker.DockerExtension
import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import io.github.zero88.qwe.gradle.QWEDecoratorPlugin
import io.github.zero88.qwe.gradle.QWEExtension
import io.github.zero88.qwe.gradle.app.QWEAppPlugin
import io.github.zero88.qwe.gradle.docker.task.DockerMultipleRegistriesPushTask
import io.github.zero88.qwe.gradle.helper.prop
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.language.base.plugins.LifecycleBasePlugin

@Suppress("UnstableApiUsage") class QWEDockerPlugin : QWEDecoratorPlugin<QWEDockerExtension> {

    override fun applyExternalPlugins(project: Project) {
        project.plugins.apply(DockerRemoteApiPlugin::class.java)
        project.plugins.apply(QWEAppPlugin::class.java)
    }

    override fun configureExtension(project: Project, qweExt: QWEExtension): QWEDockerExtension {
        val dockerExt = project.extensions.getByType<DockerExtension>()
        val ext = (dockerExt as ExtensionAware).extensions.create<QWEDockerExtension>(QWEDockerExtension.NAME)
        project.afterEvaluate {
            val name = qweExt.baseName.get()
            if (qweExt.zero88.get()) {
                ext.maintainer.set("${QWEExtension.DEV_ID} <${QWEExtension.DEV_EMAIL}>")
            }
            val registryParams = prop(project, "dockerRegistries", true)?.split(",")?.map { "${it}/${name}" }
            val tagParams = prop(project, "dockerTags")?.split(",")?.filter { s -> s.isNotEmpty() }
            val labelParams = prop(project, "dockerLabels", true)?.split(",")?.filter { s -> s.isNotEmpty() }
            val dl = listOf("version=${project.version}", "maintainer=${ext.maintainer.get()}")
            val labels = dl + (labelParams ?: listOf())
            ext.enabled.set(qweExt.application.get() && ext.enabled.get())
            ext.dockerImage.imageRegistries.addAll(registryParams ?: listOf(name))
            ext.dockerImage.tags.addAll(tagParams ?: listOf(project.version.toString()))
            ext.dockerImage.labels.putAll(labels.map { s -> s.split("=") }.map { it[0] to it[1] }.toMap())
        }
        return ext
    }

    override fun registerAndConfigureTask(project: Project, qweExt: QWEExtension, decoratorExt: QWEDockerExtension) {
        val dockerFileProvider = registerCreateDockerfileTask(project, qweExt.baseName, decoratorExt)
        registerPrintDockerfileTask(project, decoratorExt, dockerFileProvider)
        registerDockerPushTask(
            project,
            decoratorExt,
            registerDockerBuildTask(project, qweExt.baseName, decoratorExt, dockerFileProvider)
        )
    }

    private fun registerCreateDockerfileTask(
        project: Project,
        baseName: Property<String>,
        qweDockerExt: QWEDockerExtension
    ): TaskProvider<Dockerfile> {
        return project.tasks.register<Dockerfile>("createDockerfile") {
            group = "QWE Docker"
            description = "Create Dockerfile"

            onlyIf { qweDockerExt.enabled.get() }
            val fqn = baseName.get() + "-" + project.version
            val df = qweDockerExt.dockerfile
            destFile.set(qweDockerExt.outputDirectory.file(baseName))

            from(df.image.get())
            workingDir(df.appDir)
            addFile("distributions/${fqn}.tar", "./")
            runCommand("cp -rf $fqn/* ./ && rm -rf $fqn && mkdir -p ${df.dataDir.get()}")
            runCommand(df.userGroupCmd.orElse(df.generateUserGroupCmd()))
            if (df.otherCmd.isPresent) {
                runCommand(df.otherCmd)
            }
            volume(df.dataDir.get())
            user(df.user)
            entryPoint("java")
            defaultCommand("-jar", "${fqn}.jar", "-conf", "conf/${df.configFile.get()}")
            label(qweDockerExt.dockerImage.labels)
            exposePort(df.ports)
        }
    }

    private fun registerPrintDockerfileTask(
        project: Project,
        qweDockerExt: QWEDockerExtension,
        provider: TaskProvider<Dockerfile>
    ) {
        project.tasks.register<DefaultTask>("printDockerfile") {
            group = "QWE Docker"
            description = "Show Dockerfile"

            onlyIf { qweDockerExt.enabled.get() }
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
    ): TaskProvider<DockerBuildImage> {
        return project.tasks.register<DockerBuildImage>("buildDocker") {
            group = "QWE Docker"
            description = "Build Docker image"

            onlyIf { qweDockerExt.enabled.get() }
            dependsOn(project.tasks.getByName(LifecycleBasePlugin.ASSEMBLE_TASK_NAME), dockerFileProvider.get())

            inputDir.set(project.layout.buildDirectory)
            dockerFile.set(qweDockerExt.outputDirectory.file(baseName))
            images.value(qweDockerExt.dockerImage.toFQNImages())
        }
    }

    private fun registerDockerPushTask(
        project: Project,
        qweDockerExt: QWEDockerExtension,
        dockerBuildProvider: TaskProvider<DockerBuildImage>
    ) {
        project.tasks.register<DockerMultipleRegistriesPushTask>("pushDocker") {
            group = "QWE Docker"
            description = "Push Docker images to multiple remote registries"

            onlyIf { qweDockerExt.enabled.get() }
            dependsOn(dockerBuildProvider)
            images.set(qweDockerExt.dockerImage.toFQNImages())
        }
    }

}
