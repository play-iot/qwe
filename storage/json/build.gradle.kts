dependencies {
    api(project(":qwe-base"))

    testImplementation(VertxLibs.junit5)
    testImplementation(testFixtures(project(":qwe-base")))
}
