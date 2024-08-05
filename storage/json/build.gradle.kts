dependencies {
    api(projects.core)

    testImplementation(testFixtures(projects.core))
    testImplementation(libs.junit5Vertx)
    testImplementation(libs.log4j2Core)
}
