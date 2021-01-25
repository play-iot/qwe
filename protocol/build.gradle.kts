dependencies {
    api(project(":base"))
    api(UtilLibs.ip)

    testImplementation(testFixtures(project(":base")))
}
