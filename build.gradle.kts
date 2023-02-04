plugins {
    eclipse
    idea
    id(PluginLibs.oss) version PluginLibs.Version.gradlePlugin
    id(PluginLibs.root) version PluginLibs.Version.gradlePlugin
    id(PluginLibs.jooq) version PluginLibs.Version.jooq apply false
    id(PluginLibs.nexusPublish) version PluginLibs.Version.nexusPublish
}

allprojects {
    group = "cloud.playio.qwe"

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
    apply(plugin = PluginLibs.oss)

    dependencies {
        compileOnly(UtilLibs.lombok)
        annotationProcessor(UtilLibs.lombok)

        testImplementation(TestLibs.junit5Api)
        testImplementation(TestLibs.junit5Engine)
        testImplementation(TestLibs.junit5Vintage)
        testImplementation(TestLibs.jsonAssert)
        testCompileOnly(UtilLibs.lombok)
        testAnnotationProcessor(UtilLibs.lombok)
    }

    oss {
        zero88.set(true)
        publishingInfo {
            enabled.set(true)
            homepage.set("https://github.com/zero88/qwe")
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://github.com/zero88/qwe/blob/master/LICENSE")
            }
            scm {
                connection.set("scm:git:git://git@github.com:zero88/qwe.git")
                developerConnection.set("scm:git:ssh://git@github.com:zero88/qwe.git")
                url.set("https://github.com/zero88/qwe")
            }
        }
        testLogger {
            slowThreshold = 5000
        }
    }
}

nexusPublishing {
    packageGroup.set("io.github.zero88")
    repositories {
        sonatype {
            username.set(project.property("nexus.username") as String?)
            password.set(project.property("nexus.password") as String?)
        }
    }
}

tasks.register("generateJooq") {
    group = "jooq"
    dependsOn(subprojects.map { it.tasks.withType<nu.studer.gradle.jooq.JooqGenerate>() })
}
