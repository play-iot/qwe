dependencies {
    api(project(":micro:micro-metadata"))
    api(project(":micro:rpc"))
    api(project(":http:client"))
    api(VertxLibs.serviceDiscovery)
    api(VertxLibs.circuitBreaker)

    compileOnly(VertxLibs.codegen)

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":qwe-core")))
    testCompileOnly(VertxLibs.codegen)
}
