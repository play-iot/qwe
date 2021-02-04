dependencies {
    api(project(":http:metadata"))

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":base")))
}
