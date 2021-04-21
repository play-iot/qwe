dependencies {
    api(project(":qwe-core"))
    api(project(":auth"))
    api(UtilLibs.ip)

    testImplementation(testFixtures(project(":qwe-core")))
}
