dependencies {
    api(project(":micro:micro-config"))
    api(project(":micro:micro-metadata"))
    api(project(":micro:rpc"))
    api(project(":http:client"))
    api(VertxLibs.serviceDiscovery)

    implementation(project(":micro:circuit-breaker"))
    compileOnly(VertxLibs.codegen)

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":qwe-core")))
    testCompileOnly(VertxLibs.codegen)
}
