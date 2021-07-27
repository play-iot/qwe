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

    testImplementation(DatabaseLibs.jooqMeta)
    jooqGenerator(DatabaseLibs.h2)
    jooqGenerator(DatabaseLibs.jooqMetaExt)
}

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
                            Property().withKey("scripts").withValue("src/test/resources/default_schema.sql")
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
                        packageName = "io.zero88.qwe.sql.integtest.h2"
                        directory = "build/generated/h2"
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

