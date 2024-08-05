import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.jooq)
}

dependencies {
    api(projects.core)
    api(libs.jooqx)
    api(libs.jooqxSpi)
    api(libs.jooqRql)

    compileOnly(libs.jdbcVertx)
    compileOnly(libs.db2Vertx)
    compileOnly(libs.postgresVertx)
    compileOnly(libs.mysqlVertx)
    compileOnly(libs.mssqlVertx)

    testImplementation(testFixtures(projects.core))
    testImplementation(testFixtures(libs.jooqx))
    testImplementation(libs.junit5Vertx)
    testImplementation(libs.jdbcVertx)
    testImplementation(libs.db2Vertx)
    testImplementation(libs.postgresVertx)
    testImplementation(libs.mysqlVertx)
    testImplementation(libs.mssqlVertx)
    testImplementation(libs.h2Jdbc)
    testImplementation(libs.hikariCP)
    testImplementation(libs.junit5Container)
    testImplementation(libs.postgresContainer)
    testImplementation(libs.jooqMeta)
    testImplementation(libs.log4j2Core)

    jooqGenerator(libs.h2Jdbc)
    jooqGenerator(libs.postgresJdbc)
    jooqGenerator(libs.jooqMetaExt)
}
val pgHost: String? by project

jooq {
    version.set(libs.versions.jooq.jdk17.get())

    configurations {
        create("testH2Schema") {
            generateSchemaSourceOnCompilation.set(true)  // default (can be omitted)
            jooqConfiguration.apply {
                logging = Logging.INFO
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    database.apply {
                        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                        properties.add(
                            Property().withKey("scripts").withValue("src/test/resources/h2_schema.sql")
                        )
                    }
                    generate.apply {
                        isRecords = true
                        isFluentSetters = true
                        isDeprecated = false
                        isImmutablePojos = false
                        isInterfaces = false
                        isDaos = false
                        isPojos = false
                    }
                    target.apply {
                        packageName = "cloud.playio.qwe.sql.integtest.h2"
                        directory = "build/generated/h2"
                    }
                }
            }
        }

        create("testPgSchema") {
            generateSchemaSourceOnCompilation.set(true)  // default (can be omitted)
            jooqConfiguration.apply {
                logging = Logging.INFO
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = """jdbc:postgresql://${pgHost ?: "localhost"}:5423/testdb"""
                    user = "postgres"
                    password = "123"
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        properties.add(
                            Property().withKey("scripts").withValue("src/test/resources/pg_schema.sql")
                        )
                    }
                    generate.apply {
                        isRecords = true
                        isFluentSetters = true
                        isPojos = true
                        isDeprecated = false
                        isImmutablePojos = false
                        // UDT cannot generate with isInterfaces = true
                        isInterfaces = false
                        isDaos = false
                    }
                    target.apply {
                        packageName = "cloud.playio.qwe.sql.integtest.pgsql"
                        directory = "build/generated/pgsql"
                    }
                }
            }
        }
    }
}

sourceSets {
    named(SourceSet.TEST_SOURCE_SET_NAME) {
        java.srcDirs(tasks.withType<JooqGenerate>().map { it.outputDir.get().asFile })
    }
}

