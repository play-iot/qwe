dependencies {
    api(project(":http:http-metadata"))
    api(VertxLibs.web)
    api(WebLibs.jaxrs)
    api(WebLibs.jbossJaxrs)
    compileOnly(project(":micro"))
    compileOnly(VertxLibs.serviceDiscovery)

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":qwe-base")))
    testImplementation(project(":micro"))
    testImplementation(project(":http:client"))
}
