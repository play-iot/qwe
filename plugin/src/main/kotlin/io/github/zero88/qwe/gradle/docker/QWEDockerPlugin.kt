package io.github.zero88.qwe.gradle.docker

import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import io.github.zero88.qwe.gradle.QWEPlugin
import org.gradle.api.Project

class QWEDockerPlugin : QWEPlugin() {

    override fun applyPlugins(project: Project) {
        super.applyPlugins(project)
        project.pluginManager.apply(DockerRemoteApiPlugin::class.java)
    }

    override fun doApply(project: Project, rootExtName: String) {
    }
}
