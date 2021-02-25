dependencies {
    api(VertxLibs.core)
    api(LogLibs.slf4j)

    testImplementation(testFixtures(project(":qwe-base")))
}
