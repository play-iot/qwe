dependencies {
    api(project(":base"))

    testImplementation(VertxLibs.junit5)
    testImplementation(testFixtures(project(":base")))
}
