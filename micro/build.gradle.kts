
dependencies {
    api(project(":http"))
    api(project(":http:client"))
    api(VertxLibs.serviceDiscovery)
    api(VertxLibs.circuitBreaker)

}
