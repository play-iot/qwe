dependencies {
    api(project(":qwe-core"))
    api(ZeroLibs.jooqx)
    api(ZeroLibs.jooqxSpi)
    api(ZeroLibs.jooqRql)

    compileOnly(VertxLibs.codegen)

    compileOnly(VertxLibs.sqlClient)
    compileOnly(VertxLibs.jdbc)
    compileOnly(VertxLibs.db2)
    compileOnly(VertxLibs.pgsql)
    compileOnly(VertxLibs.mysql)
    compileOnly(VertxLibs.mssql)

    compileOnly(DatabaseLibs.h2)
    compileOnly(DatabaseLibs.hikari)
    compileOnly(DatabaseLibs.agroalPool)

    testImplementation(VertxLibs.junit5)
    testImplementation(testFixtures(project(":qwe-core")))
}
