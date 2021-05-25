import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

plugins {
    id(PluginLibs.docker)
}

dependencies {
    api(project(":examples:shared"))
    api(VertxLibs.ignite)
}

oss {
    publishingInfo {
        enabled.set(false)
    }
}

qwe {
    app {
        appVerticle.set("io.zero88.qwe.example.docker.DockerVerticle")
    }
    systemd {
        enabled.set(false)
    }
    docker {
        dockerfile {
            image.set("openjdk:11-jre-slim")
        }
        dockerImage {
            labels.put("current", OffsetDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
        }
    }
}

