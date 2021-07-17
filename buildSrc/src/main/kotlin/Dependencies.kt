object UtilLibs {

    object Version {

        const val lombok = "1.18.16"
        const val classgraph = "4.8.105"
        const val jetbrainsAnnotations = "20.1.0"
    }

    const val lombok = "org.projectlombok:lombok:${Version.lombok}"
    const val jetbrainsAnnotations = "org.jetbrains:annotations:${Version.jetbrainsAnnotations}"
}

object PluginLibs {

    object Version {

        const val jvm = "1.3.72"
        const val nexusPublish = "1.1.0"
        const val plugin = "2.0.0"
    }

    const val jvm = "jvm"
    const val nexusPublish = "io.github.gradle-nexus.publish-plugin"
    const val oss = "io.github.zero88.gradle.oss"
    const val root = "io.github.zero88.gradle.root"
    const val app = "io.github.zero88.gradle.qwe.app"
    const val docker = "io.github.zero88.gradle.qwe.docker"
}

object WebLibs {

    object Version {

        const val jaxrs = "2.1.1"
    }

    const val jaxrs = "javax.ws.rs:javax.ws.rs-api:${Version.jaxrs}"
}

object JacksonLibs {

    object Version {

        const val jackson = "2.12.0"
    }

    const val core = "com.fasterxml.jackson.core:jackson-core:${Version.jackson}"
    const val annotations = "com.fasterxml.jackson.core:jackson-annotations:${Version.jackson}"
    const val databind = "com.fasterxml.jackson.core:jackson-databind:${Version.jackson}"
    const val datatype = "com.fasterxml.jackson.datatype:jackson-datatype-jdk8:${Version.jackson}"
    const val datetime = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Version.jackson}"
}

object TestLibs {

    object Version {

        const val junit5 = "5.7.0"
        const val jsonAssert = "1.5.0"
    }

    const val junit5Api = "org.junit.jupiter:junit-jupiter-api:${Version.junit5}"
    const val junit5Engine = "org.junit.jupiter:junit-jupiter-engine:${Version.junit5}"
    const val junit5Vintage = "org.junit.vintage:junit-vintage-engine:${Version.junit5}"
    const val jsonAssert = "org.skyscreamer:jsonassert:${Version.jsonAssert}"
}

object VertxLibs {

    object Version {

        const val vertx = "4.0.3"
    }

    const val core = "io.vertx:vertx-core:${Version.vertx}"
    const val codegen = "io.vertx:vertx-codegen:${Version.vertx}"
    const val config = "io.vertx:vertx-config:${Version.vertx}"
    const val web = "io.vertx:vertx-web:${Version.vertx}"
    const val serviceDiscovery = "io.vertx:vertx-service-discovery:${Version.vertx}"
    const val circuitBreaker = "io.vertx:vertx-circuit-breaker:${Version.vertx}"
    const val healthCheck = "io.vertx:vertx-health-check:${Version.vertx}"

    const val hazelcast = "io.vertx:vertx-hazelcast:${Version.vertx}"
    const val zookeeper = "io.vertx:vertx-zookeeper:${Version.vertx}"
    const val ignite = "io.vertx:vertx-ignite:${Version.vertx}"
    const val infinispan = "io.vertx:vertx-infinispan:${Version.vertx}"

    const val rx2 = "io.vertx:vertx-rx-java2:${Version.vertx}"
    const val junit = "io.vertx:vertx-unit:${Version.vertx}"
    const val junit5 = "io.vertx:vertx-junit5:${Version.vertx}"

}

object LogLibs {

    object Version {

        const val slf4j = "1.7.30"
        const val logback = "1.2.3"
    }

    const val slf4j = "org.slf4j:slf4j-api:${Version.slf4j}"
    const val logback = "ch.qos.logback:logback-classic:${Version.logback}"
}

object ZeroLibs {
    object Version {

        const val utils = "2.0.0-SNAPSHOT"
        const val jpaExt = "0.9.0"
    }

    const val utils = "io.github.zero88:java-utils:${Version.utils}"
    const val jpaExt = "io.github.zero88:jpa-ext:${Version.jpaExt}"

}
