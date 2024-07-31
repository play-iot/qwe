import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.docker)
}
dependencies {
    api(project(":examples:shared"))
}

qwe {
    app {
        appVerticle.set("cloud.playio.qwe.example.docker.DockerVerticle")
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

