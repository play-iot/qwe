dependencies {
    api(project(":qwe-core"))
    api(VertxLibs.circuitBreaker)

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":qwe-core")))
}
