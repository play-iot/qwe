dependencies {
    api(project(":http"))
    api(VertxLibs.web)
//    compile "io.vertx:vertx-web-api-contract:$project.versions.vertx"
    api("com.zandero:rest.vertx:0.9.1") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("org.jboss.resteasy:resteasy-jaxrs:3.6.2.Final")
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
