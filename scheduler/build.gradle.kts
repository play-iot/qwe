dependencies {
    api(project(":base"))
    api(UtilLibs.quartz) {
        exclude("com.mchange")
        exclude("com.zaxxer")
    }

    testImplementation(TestLibs.junit)
    testImplementation(TestLibs.junit5Vintage)
    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":base")))
}
