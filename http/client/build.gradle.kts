dependencies {
    api(project(":http"))

    testImplementation(TestLibs.junit)
    testImplementation(TestLibs.junit5Vintage)

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":base")))
}
