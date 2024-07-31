dependencies {
    api(projects.core)
    api(libs.vertxCircuitBreaker)

    testImplementation(libs.junitVertx)
    testImplementation(testFixtures(projects.core))
}
