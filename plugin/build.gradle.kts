plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("Docker plugin") {
            id = "zero88.qwe.gradle.docker"
            implementationClass = "io.github.zero88.qwe.gradle.docker.QWEDockerPlugin"
        }
        create("Generator plugin") {
            id = "zero88.qwe.gradle.generator"
            implementationClass = "io.github.zero88.qwe.gradle.generator.QWEGeneratorPlugin"
        }
        create("Root project plugin") {
            id = "zero88.qwe.gradle.root"
            implementationClass = "io.github.zero88.qwe.gradle.QWERootProjectPlugin"
        }
    }
}

dependencies {
    api(PluginLibs.Depends.docker)
    api(PluginLibs.Depends.sonarQube)
}
