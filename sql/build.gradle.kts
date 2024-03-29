import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

plugins {
    id(PluginLibs.jooq)
}

dependencies {
    api(project(":qwe-core"))
    api(ZeroLibs.jooqx)
    api(ZeroLibs.jooqxSpi)
    api(ZeroLibs.jooqRql)

    compileOnly(VertxLibs.codegen)

    compileOnly(VertxLibs.jdbc)
    compileOnly(VertxLibs.db2)
    compileOnly(VertxLibs.pgsql)
    compileOnly(VertxLibs.mysql)
    compileOnly(VertxLibs.mssql)

    testImplementation(VertxLibs.junit5)
    testImplementation(VertxLibs.jdbc)
    testImplementation(VertxLibs.db2)
    testImplementation(VertxLibs.pgsql)
    testImplementation(VertxLibs.mysql)
    testImplementation(VertxLibs.mssql)
    testImplementation(DatabaseLibs.h2)
    testImplementation(DatabaseLibs.hikari)
    testImplementation(testFixtures(project(":qwe-core")))
    testImplementation(testFixtures(ZeroLibs.jooqx))
    testImplementation(TestContainers.junit5)
    testImplementation(TestContainers.pgsql)

    testImplementation(DatabaseLibs.jooqMeta)
    jooqGenerator(DatabaseLibs.h2)
    jooqGenerator(DatabaseLibs.pgsql)
    jooqGenerator(DatabaseLibs.jooqMetaExt)
}
val pgHost: String? by project

jooq {
    version.set(DatabaseLibs.Version.jooq)

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

