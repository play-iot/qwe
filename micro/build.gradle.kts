dependencies {
    api(project(":micro:micro-metadata"))
    api(project(":micro:rpc"))
    api(project(":http:client"))
    api(VertxLibs.serviceDiscovery)
    api(VertxLibs.circuitBreaker)

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":qwe-base")))
}
