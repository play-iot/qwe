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
    }
}

dependencies {
    api(PluginLibs.Depends.docker)
}
