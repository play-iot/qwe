dependencies {
    api(project(":qwe-core"))
    api(UtilLibs.ip)

    testImplementation(testFixtures(project(":qwe-core")))
}
