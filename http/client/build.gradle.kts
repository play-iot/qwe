dependencies {
    api(projects.core)

    testImplementation(libs.junitVertx)
    testImplementation(testFixtures(projects.core))
}
