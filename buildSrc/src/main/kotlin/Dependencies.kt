object UtilLibs {

    object Version {

        const val lombok = "1.18.16"
        const val classgraph = "4.6.23"
        const val oshi = "3.13.3"
    }

    const val lombok = "org.projectlombok:lombok:${Version.lombok}"
    const val oshi = "com.github.oshi:oshi-core:${Version.oshi}"
    const val classgraph = "io.github.classgraph:classgraph:${Version.classgraph}"
}

object PluginLibs {

    object Version {

        const val sonarQube = "3.0"
        const val docker = "6.6.1"
        const val nexusStaging = "0.22.0"
    }

    const val docker = "com.bmuschko.docker-remote-api"
    const val sonarQube = "org.sonarqube"
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

        const val junit = "4.12"
        const val junit5 = "5.7.0"
        const val jsonAssert = "1.5.0"
    }

    const val junit = "junit:junit:${Version.junit}"
    const val junit5Api = "org.junit.jupiter:junit-jupiter-api:${Version.junit5}"
    const val junit5Engine = "org.junit.jupiter:junit-jupiter-engine:${Version.junit5}"
    const val junit5Vintage = "org.junit.vintage:junit-vintage-engine:${Version.junit5}"
    const val jsonAssert = "org.skyscreamer:jsonassert:${Version.jsonAssert}"
}

object VertxLibs {

    object Version {

        const val vertx = "3.9.2"
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

object DatabaseLibs {

    object Version {

        const val jooq = "3.14.0"
        const val h2 = "1.4.200"
        const val pgsql = "42.2.5"
        const val hikari = "3.2.0"
        const val jooqVertx = ""
        const val jpa = "2.2"
        const val jta = "1.3"
    }

    const val h2 = "com.h2database:h2:${Version.h2}"
    const val pgsql = "org.postgresql:postgresql:${Version.pgsql}"
    const val hikari = "com.zaxxer:HikariCP:${Version.hikari}"
    const val jpa = "javax.persistence:javax.persistence-api:${Version.jpa}"
    const val jta = "javax.transaction:javax.transaction-api:${Version.jta}"
    const val jooq = "org.jooq:jooq:${Version.jooq}"
    const val jooqMeta = "org.jooq:jooq-meta:${Version.jooq}"
    const val jooqMetaExt = "org.jooq:jooq-meta-extensions:${Version.jooq}"
    const val jooqCodegen = "org.jooq:jooq-codegen:${Version.jooq}"
    const val jooqVertxRx = "io.github.jklingsporn:vertx-jooq-rx:${Version.jooqVertx}"
    const val jooqVertxShared = "io.github.jklingsporn:vertx-jooq-shared:${Version.jooqVertx}"
    const val jooqVertxCodegen = "io.github.jklingsporn:vertx-jooq-generate:${Version.jooqVertx}"
}

object ZeroLibs {
    object Version {

        const val utils = "1.0.1"
        const val jpaExt = "0.9.0"
    }

    const val utils = "io.github.zero88:java-utils:${Version.utils}"
    const val jpaExt = "io.github.zero88:jpa-ext:${Version.jpaExt}"
}
