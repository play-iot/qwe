dependencies {
    api(projects.core)
    api(projects.http.shared)

    testImplementation(testFixtures(projects.core))
    testImplementation(libs.junitVertx)
    testImplementation(libs.log4j2Core)
}
