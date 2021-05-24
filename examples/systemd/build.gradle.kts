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
        appName.set("kaka")
        appVerticle.set("io.zero88.qwe.example.systemd.SystemdVerticle")
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
