dependencies {
    api(VertxLibs.core)
    api(LogLibs.slf4j)
    compileOnly(JacksonLibs.databind)

    testImplementation(testFixtures(project(":qwe-base")))
}
