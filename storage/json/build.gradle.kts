dependencies {
    api(project(":qwe-core"))

    testImplementation(VertxLibs.junit5)
    testImplementation(testFixtures(project(":qwe-core")))
}
