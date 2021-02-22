dependencies {
    api(project(":qwe-base"))
    api(project(":auth"))
    api(UtilLibs.ip)

    testImplementation(testFixtures(project(":qwe-base")))
}
