dependencies {
    api(project(":base"))

    testImplementation(TestLibs.jsonAssert)
    testImplementation(TestLibs.junit)
    testImplementation(VertxLibs.junit)
}
