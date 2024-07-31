@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.app)
}

dependencies {
    api(project(":examples:shared"))
    api(project(":validator"))
}

qwe {
    app {
        appVerticle.set("cloud.playio.qwe.example.fatjar.FatJarVerticle")
        appLauncher.set("cloud.playio.qwe.QWELauncher")
        fatJar.set(true)
        logging {
            otherLoggers.set(
                mapOf(
                    "io.netty" to "info",
                    "io.vertx" to "info",
                    "com.hazelcast" to "info",
                    "io.zero88" to "debug"
                )
            )
        }
    }
    systemd {
        enabled.set(false)
    }
}

