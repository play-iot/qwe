dependencies {
    api(project(":micro:metadata"))
    api(project(":micro:rpc"))
    api(project(":http:client"))
    api(VertxLibs.serviceDiscovery)
    api(VertxLibs.circuitBreaker)

    testImplementation(TestLibs.junit)
    testImplementation(TestLibs.junit5Vintage)
    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":base")))
}
