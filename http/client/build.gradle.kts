dependencies {
    api(project(":http"))

    testImplementation(TestLibs.jsonAssert)
    testImplementation(TestLibs.junit)
    testImplementation(VertxLibs.junit)

    testImplementation(testFixtures(project(":base")))
}
