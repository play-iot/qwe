dependencies {
    api(projects.core)
    api(libs.vertxCircuitBreaker)

    testImplementation(testFixtures(projects.core))
    testImplementation(libs.junitVertx)
    testImplementation(libs.log4j2Core)
}
