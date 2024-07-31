dependencies {
    api(projects.core)

    testImplementation(libs.junit5Vertx)
    testImplementation(testFixtures(projects.core))
}
