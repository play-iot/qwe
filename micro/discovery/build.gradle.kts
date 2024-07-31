
import cloud.playio.gradle.generator.codegen.SourceSetName

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.codegen)
}

codegen {
    vertx {
        version.set(libs.vertxCore.get().version)
        sources.addAll(arrayOf(SourceSetName.MAIN))
    }
}

dependencies {
    api(project(":micro:config"))
    api(project(":micro:metadata"))
    api(project(":micro:rpc"))
    api(project(":http:client"))
    api(libs.vertxServiceDiscovery)

    testImplementation(libs.junitVertx)
    testImplementation(testFixtures(projects.core))
}

