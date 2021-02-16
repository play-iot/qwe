package io.github.zero88.qwe.gradle.helper

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import java.util.*

fun getPluginResource(project: Project, fileName: String): Pair<FileCollection, Boolean> {
    val classLoader = Thread.currentThread().contextClassLoader
    val url = classLoader.getResource(fileName)?.toString()
    assert(url != null) { "Not found file $fileName in current context" }
    val isInsideJar = url!!.startsWith("jar:")
    val from = if (isInsideJar) project.zipTree(
        url.removePrefix("jar:").removeSuffix("!/$fileName")
    ) else project.files(url)
    return Pair(from, isInsideJar)
}

fun readResourceProperties(fileName: String): Properties? {
    val classLoader = Thread.currentThread().contextClassLoader
    return classLoader.getResourceAsStream(fileName)?.use { Properties().apply { load(it) } }
}
