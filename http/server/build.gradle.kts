dependencies {
    api(project(":http"))
    api(VertxLibs.web)
//    compile "io.vertx:vertx-web-api-contract:$project.versions.vertx"
    api("com.zandero:rest.vertx:0.9.1") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    api("javax.ws.rs:javax.ws.rs-api:2.1.1")
    compileOnly(project(":micro"))
    compileOnly(VertxLibs.serviceDiscovery)

    testImplementation(TestLibs.jsonAssert)
    testImplementation(TestLibs.junit)
    testImplementation(TestLibs.junit5Vintage)
    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":base")))
    testImplementation(project(":micro"))
    testImplementation(project(":http:client"))
}
