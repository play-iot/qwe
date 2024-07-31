@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.app)
}

dependencies {
    api(project(":examples:shared"))
}

qwe {
    app {
        appName.set("kaka")
        appVerticle.set("cloud.playio.qwe.example.systemd.SystemdVerticle")
//        fatJar.set(true)
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
        serviceName.set("qwe-nix")
    }
}
