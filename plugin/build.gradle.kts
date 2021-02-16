plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id(PluginLibs.gradlePluginPublish) version PluginLibs.Version.gradlePluginPublish
}

gradlePlugin {
    plugins {
        create("Generator plugin") {
            id = "zero88.qwe.gradle.generator"
            displayName = "QWE Generator plugin"
            description = "This plugin adds Generator capabilities to generate config/logging/systemd service for QWE"
            implementationClass = "io.github.zero88.qwe.gradle.generator.QWEGeneratorPlugin"
        }
        create("Docker plugin") {
            id = "zero88.qwe.gradle.docker"
            displayName = "QWE Docker plugin"
            description = "This plugin adds Docker capabilities to build/push Docker image for QWE application"
            implementationClass = "io.github.zero88.qwe.gradle.docker.QWEDockerPlugin"
        }
        create("Root project plugin") {
            id = "zero88.qwe.gradle.root"
            displayName = "QWE Root Project plugin"
            description = "This plugin adds some utilities in root project in a multi-project build"
            implementationClass = "io.github.zero88.qwe.gradle.QWERootProjectPlugin"
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

