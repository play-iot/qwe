plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id(PluginLibs.gradlePluginPublish) version PluginLibs.Version.gradlePluginPublish
}

gradlePlugin {
    plugins {
        create("OSS project plugin") {
            id = "io.github.zero88.qwe.gradle.oss"
            displayName = "QWE OSS Project plugin"
            description = "This plugin adds some utilities in project for build/maven distribution"
            implementationClass = "io.github.zero88.qwe.gradle.QWEOSSProjectPlugin"
        }
        create("OSS Root project plugin") {
            id = "io.github.zero88.qwe.gradle.root"
            displayName = "QWE Root Project plugin"
            description = "This plugin adds some utilities in root project in a multi-project build"
            implementationClass = "io.github.zero88.qwe.gradle.QWERootProjectPlugin"
        }
        create("QWE Application plugin") {
            id = "io.github.zero88.qwe.gradle.app"
            displayName = "QWE Application plugin"
            description = "This plugin adds Generator/Bundle capabilities to QWE Application"
            implementationClass = "io.github.zero88.qwe.gradle.app.QWEAppPlugin"
        }
        create("QWE Docker plugin") {
            id = "io.github.zero88.qwe.gradle.docker"
            displayName = "QWE Docker plugin"
            description = "This plugin adds Docker capabilities to build/push Docker image for QWE application"
            implementationClass = "io.github.zero88.qwe.gradle.docker.QWEDockerPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/zero88/qwe/blob/main/plugin/README.md"
    vcsUrl = "https://github.com/zero88/qwe.git"
    tags = listOf("qwe", "generator", "docker")
}

dependencies {
    api(PluginLibs.Depends.docker)
    api(PluginLibs.Depends.sonarQube)
}

