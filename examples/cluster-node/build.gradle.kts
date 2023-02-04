plugins {
    id(PluginLibs.app)
}

dependencies {
    api(project(":examples:shared"))
    api(VertxLibs.hazelcast)
}

oss {
    publishingInfo {
        enabled.set(false)
    }
}

qwe {
    app {
        appName.set("qwe-example-boot")
        appVerticle.set("cloud.playio.qwe.example.boot.BootVerticle")
        fatJar.set(true)
        logging {
            otherLoggers.set(
                mapOf(
                    "io.netty" to "info",
                    "io.vertx" to "info",
                    "com.hazelcast" to "info",
                    "io.zero88" to "info"
                )
            )
        }
    }
    systemd {
        serviceName.set("qwe-boot")
    }
}
