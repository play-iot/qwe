plugins {
    id(ZeroLibs.Plugins.oss) version ZeroLibs.Version.plugin
    id(ZeroLibs.Plugins.root) version ZeroLibs.Version.plugin apply false
    id(PluginLibs.nexusStaging) version PluginLibs.Version.nexusStaging
}

allprojects {
    group = "io.github.zero88.qwe"

    repositories {
        mavenLocal()
        maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
        maven { url = uri("https://maven.pkg.github.com/zero88/java-utils") }
        mavenCentral()
        jcenter()
    }
}

apply(plugin = ZeroLibs.Plugins.root)

subprojects {
    apply(plugin = "eclipse")
    apply(plugin = "idea")
    apply(plugin = ZeroLibs.Plugins.oss)

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

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

    qwe {
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
    }
}

nexusStaging {
    packageGroup = "io.github.zero88"
    username = project.property("nexus.username") as String?
    password = project.property("nexus.password") as String?
}
