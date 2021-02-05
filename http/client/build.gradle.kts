dependencies {
    api(project(":http:http-metadata"))

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":qwe-base")))
}
