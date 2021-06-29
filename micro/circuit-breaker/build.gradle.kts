dependencies {
    api(project(":micro:micro-config"))
    api(VertxLibs.circuitBreaker)

    compileOnly(VertxLibs.codegen)

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":qwe-core")))
}
