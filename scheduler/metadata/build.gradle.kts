dependencies {
    api(project(":qwe-base"))
    implementation(UtilLibs.quartz) {
        exclude("com.mchange")
        exclude("com.zaxxer")
    }
}
