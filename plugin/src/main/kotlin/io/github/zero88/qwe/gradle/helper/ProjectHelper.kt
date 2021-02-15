package io.github.zero88.qwe.gradle.helper

import org.gradle.api.Project

fun prop(project: Project, key: String, forceNull: Boolean = false): String? {
    val prop = prop(project, key, "")
    return if (prop == "" && forceNull) null else prop
}

fun prop(project: Project, key: String, fallback: String?): String {
    val fb = fallback ?: ""
    return if (project.hasProperty(key)) project.property(key) as String? ?: fb else fb
}

fun computeBaseName(project: Project): String {
    return computeProjectName(project, "-")
}

private fun computeProjectName(project: Project, sep: String, firstSep: String? = null): String {
    if (project.parent == null) {
        return prop(project, "baseName", project.name)
    }
    val s = if (project.parent?.parent == null && firstSep != null) firstSep else sep
    return computeProjectName(project.parent!!, sep, firstSep) + s + project.projectDir.name
}
