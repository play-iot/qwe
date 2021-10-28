dependencies {
    api(project(":qwe-core"))
    api(VertxLibs.web)
    api(JSRLibs.jaxrs)
    
    compileOnly(project(":micro:discovery"))
    compileOnly(VertxLibs.serviceDiscovery)
    compileOnly(VertxLibs.jwt)
    compileOnly(VertxLibs.ldap)
    compileOnly(VertxLibs.oauth2)
    compileOnly(VertxLibs.shiro)
    compileOnly(VertxLibs.webauthn)

    testImplementation(VertxLibs.junit)
    testImplementation(VertxLibs.webClient)
    testImplementation(testFixtures(project(":qwe-core")))
    testImplementation(project(":micro:discovery"))
    testImplementation(project(":http:client"))

    testImplementation(VertxLibs.jwt)
    testImplementation(VertxLibs.ldap)
    testImplementation(VertxLibs.oauth2)
    testImplementation(VertxLibs.shiro)
    testImplementation(VertxLibs.webauthn)
}
