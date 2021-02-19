package io.github.zero88.qwe.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

interface QWEDecoratorPlugin<T> : Plugin<Project> {

    override fun apply(project: Project) {
        applyExternalPlugins(project)
        val qweExtension = project.extensions.getByType<QWEExtension>()
        registerAndConfigureTask(project, qweExtension, configureExtension(project, qweExtension))
    }

    fun applyExternalPlugins(project: Project)

    fun configureExtension(project: Project, qweExt: QWEExtension): T

    fun registerAndConfigureTask(project: Project, qweExt: QWEExtension, decoratorExt: T)
}
