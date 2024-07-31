import cloud.playio.gradle.NexusConfig
import cloud.playio.gradle.NexusVersion

@Suppress("DSL_SCOPE_VIOLATION") // workaround for gradle v7
plugins {
    eclipse
    idea
    alias(libs.plugins.oss)
    alias(libs.plugins.root)
    alias(libs.plugins.antora) apply false
    alias(libs.plugins.codegen) apply false
    alias(libs.plugins.docgen) apply false
    alias(libs.plugins.app) apply false
    alias(libs.plugins.docker) apply false
    alias(libs.plugins.jooq) apply false
}

project.ext.set("baseName", (gradle as ExtensionAware).extensions["BASE_NAME"] as String)
project.ext.set(NexusConfig.NEXUS_VERSION_KEY, NexusVersion.BEFORE_2021_02_24)

allprojects {
    group = project.ext.get("projectGroup") as String

    repositories {
        mavenLocal()
        maven { url = uri("https://maven-central-asia.storage-download.googleapis.com/maven2/") }
        maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
        mavenCentral()
    }

    val skipPublish = (gradle as ExtensionAware).extensions["SKIP_PUBLISH"] as Array<*>
    sonarqube {
        isSkipProject = project.path in skipPublish
    }

    tasks {
        withType<AbstractPublishToMaven> {
            enabled = project != rootProject && project.path !in skipPublish
        }
    }
}

subprojects {
    apply(plugin = rootProject.libs.plugins.oss.get().pluginId)

    dependencies {
        compileOnly(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)
        testCompileOnly(rootProject.libs.lombok)
        testAnnotationProcessor(rootProject.libs.lombok)

        compileOnly(rootProject.libs.jetbrainsAnnotations)
        testCompileOnly(rootProject.libs.jetbrainsAnnotations)

        testImplementation(rootProject.libs.bundles.junit5)
        testImplementation(rootProject.libs.junitPioneer)
        testImplementation(rootProject.libs.jsonAssert)
    }

    oss {
        playio.set(true)
        github.set(true)
        testLogger {
            slowThreshold = 5000
        }
    }


    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(JavaVersion.current().majorVersion))
        }
    }
}

tasks.register("generateJooq") {
    group = "jooq"
    dependsOn(subprojects.map { it.tasks.withType<nu.studer.gradle.jooq.JooqGenerate>() })
}
