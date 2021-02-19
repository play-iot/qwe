package io.github.zero88.qwe.gradle.app

import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory

@Suppress("UnstableApiUsage")
open class GeneratedLayoutExtension(objects: ObjectFactory, layout: ProjectLayout) {

    val generatedDir = objects.directoryProperty().convention(layout.buildDirectory.dir("generated"))
    val generatedConfigDir = objects.directoryProperty().convention(generatedDir.dir("conf"))
    val generatedServiceDir = objects.directoryProperty().convention(generatedDir.dir("service"))
    val generatedSrcDir = objects.directoryProperty().convention(generatedDir.dir("main"))
    val generatedJavaSrcDir = objects.directoryProperty().convention(generatedSrcDir.dir("java"))
    val generatedKotlinSrcDir = objects.directoryProperty().convention(generatedSrcDir.dir("kotlin"))
    val generatedResourceDir = objects.directoryProperty().convention(generatedSrcDir.dir("resources"))
    val generatedTestDir = objects.directoryProperty().convention(generatedDir.dir("test"))
    val generatedJavaSrcTestDir = objects.directoryProperty().convention(generatedTestDir.dir("java"))
    val generatedKotlinSrcTestDir = objects.directoryProperty().convention(generatedTestDir.dir("kotlin"))
    val generatedResourceTestDir = objects.directoryProperty().convention(generatedTestDir.dir("resources"))
}
