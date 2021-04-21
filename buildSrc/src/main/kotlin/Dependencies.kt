object UtilLibs {

    object Version {

        const val lombok = "1.18.16"
        const val classgraph = "4.8.98"
        const val ip = "5.3.3"
        const val quartz = "2.3.2"
    }

    const val lombok = "org.projectlombok:lombok:${Version.lombok}"
    const val classgraph = "io.github.classgraph:classgraph:${Version.classgraph}"
    const val ip = "com.github.seancfoley:ipaddress:${Version.ip}"
    const val quartz = "org.quartz-scheduler:quartz:${Version.quartz}"
}

object WebLibs {

    object Version {

        const val jaxrs = "2.1.1"
        const val jbossJaxrs = "2.0.1.Final"
    }

    const val jaxrs = "javax.ws.rs:javax.ws.rs-api:${Version.jaxrs}"
    const val jbossJaxrs = "org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_2.1_spec:${Version.jbossJaxrs}"
}

object PluginLibs {

    object Version {

        const val nexusStaging = "0.22.0"
    }

    const val nexusStaging = "io.codearte.nexus-staging"
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

        const val vertx = "4.0.0"
    }

    const val core = "io.vertx:vertx-core:${Version.vertx}"
    const val codegen = "io.vertx:vertx-codegen:${Version.vertx}"
    const val config = "io.vertx:vertx-config:${Version.vertx}"
    const val web = "io.vertx:vertx-web:${Version.vertx}"
    const val serviceDiscovery = "io.vertx:vertx-service-discovery:${Version.vertx}"
    const val circuitBreaker = "io.vertx:vertx-circuit-breaker:${Version.vertx}"
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

        const val utils = "1.0.1"
        const val jpaExt = "0.9.0"
        const val plugin = "1.0.0-SNAPSHOT"
    }

    const val utils = "io.github.zero88:java-utils:${Version.utils}"
    const val jpaExt = "io.github.zero88:jpa-ext:${Version.jpaExt}"

    object Plugins {

        const val oss = "io.github.zero88.qwe.gradle.oss"
        const val root = "io.github.zero88.qwe.gradle.root"
    }
}
