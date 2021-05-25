plugins {
    id(PluginLibs.app)
}

dependencies {
    api(project(":examples:shared"))
}

oss {
    publishingInfo {
        enabled.set(false)
    }
}

qwe {
    app {
        appVerticle.set("io.zero88.qwe.example.fatjar.FatJarVerticle")
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

