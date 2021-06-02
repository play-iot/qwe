dependencies {
    api(project(":micro:micro-metadata"))
    api(project(":micro:rpc"))
    api(project(":http:client"))
    api(VertxLibs.serviceDiscovery)
    api(VertxLibs.circuitBreaker)
    api("io.vertx:vertx-service-discovery-backend-redis:4.0.3")
    api("io.vertx:vertx-service-discovery-bridge-docker:4.0.3")

    compileOnly(VertxLibs.codegen)

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":qwe-core")))
    testCompileOnly(VertxLibs.codegen)
}
