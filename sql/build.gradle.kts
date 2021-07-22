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
    testImplementation(VertxLibs.sqlClient)
    testImplementation(VertxLibs.jdbc)
    testImplementation(VertxLibs.db2)
    testImplementation(VertxLibs.pgsql)
    testImplementation(VertxLibs.mysql)
    testImplementation(VertxLibs.mssql)
    testImplementation(DatabaseLibs.h2)
    testImplementation(DatabaseLibs.hikari)
    testImplementation(DatabaseLibs.agroalPool)
    testImplementation(testFixtures(project(":qwe-core")))
}
