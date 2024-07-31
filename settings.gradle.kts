/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/6.7/userguide/multi_project_builds.html
 */

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val projectName = "qwe"
val profile: String by settings
val pools = mutableMapOf(
    projectName to arrayOf(":core", ":ext"),
    "http" to arrayOf("http:client", "http:server", "http:openapi"),
    "sql" to arrayOf("sql" /*"sql:api", "sql:type"*/),
    "micro" to arrayOf(
        ":micro:config",
        ":micro:metadata",
        ":micro:circuit-breaker",
        ":micro:discovery",
        ":micro:rpc"
    ),
    "storage" to arrayOf("storage:json"),
    "validator" to arrayOf("validator"),
    "examples" to arrayOf(
        "examples:shared",
        "examples:systemd",
        "examples:docker",
        "examples:fatjar",
        "examples:cluster-node"
    ),
    "integtest" to emptyArray()
)
val docs = arrayOf(":docs")
val excludeCISonar = docs
val excludeCIBuild = pools["examples"]!! + pools["integtest"]!! + excludeCISonar
pools.putAll(mapOf("$projectName:docs" to pools[projectName]!!.plus(docs)))

fun flatten(): List<String> = pools.values.toTypedArray().flatten()

rootProject.name = "$projectName-parent"
when {
    profile.isBlank() || profile == "all" -> flatten().toTypedArray()
    profile == "ciBuild"                  -> flatten().filter { !excludeCIBuild.contains(it) }.toTypedArray()
    profile == "ciSonar"                  -> flatten().filter { !excludeCISonar.contains(it) }.toTypedArray()
    else                                  -> pools.getOrElse(profile) { throw IllegalArgumentException("Not found profile[$profile]") }
}.forEach { include(it) }
//project(":micro:micro-config").projectDir = file("micro/config")
//project(":micro:micro-metadata").projectDir = file("micro/metadata")

if (gradle is ExtensionAware) {
    val extensions = (gradle as ExtensionAware).extensions
    extensions.add("BASE_NAME", projectName)
    extensions.add("PROJECT_POOL", pools.toMap())
    extensions.add("SKIP_PUBLISH", excludeCIBuild + arrayOf(":docs", ":sample", ":integtest"))
}
