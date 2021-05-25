dependencies {
    api(project(":qwe-core"))

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":qwe-core")))
}
