dependencies {
    api(project(":http:metadata"))
    api(VertxLibs.web)
//    compile "io.vertx:vertx-web-api-contract:$project.versions.vertx"
    api("javax.ws.rs:javax.ws.rs-api:2.1.1")
    compileOnly(project(":micro"))
    compileOnly(VertxLibs.serviceDiscovery)

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":base")))
    testImplementation(project(":micro"))
    testImplementation(project(":http:client"))
}
