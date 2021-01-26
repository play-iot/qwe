dependencies {
    api(project(":base"))

    testImplementation(testFixtures(project(":base")))
    testImplementation(VertxLibs.junit5)
}
