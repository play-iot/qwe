dependencies {
    api(project(":scheduler:metadata"))
    api(UtilLibs.quartz) {
        exclude("com.mchange")
        exclude("com.zaxxer")
    }

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":base")))
}
