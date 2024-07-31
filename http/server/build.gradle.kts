dependencies {
    api(projects.core)
    api(libs.vertxWeb)
    api(libs.javaxJaxrs)

    compileOnly(libs.vertxCodegen)

    compileOnly(projects.micro.discovery)
    compileOnly(libs.vertxServiceDiscovery)
    compileOnly(libs.vertxJwt)
    compileOnly(libs.vertxLdap)
    compileOnly(libs.vertxOauth2)
    compileOnly(libs.vertxShiro)
    compileOnly(libs.vertxWebauthn)

    testImplementation(libs.junitVertx)
    testImplementation(libs.vertxWebClient)
    testImplementation(testFixtures(projects.core))
    testImplementation(projects.micro.discovery)
    testImplementation(projects.http.client)

    testImplementation(libs.vertxJwt)
    testImplementation(libs.vertxLdap)
    testImplementation(libs.vertxOauth2)
    testImplementation(libs.vertxShiro)
    testImplementation(libs.vertxWebauthn)
}
