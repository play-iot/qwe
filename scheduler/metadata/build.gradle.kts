dependencies {
    api(project(":base"))
    implementation(UtilLibs.quartz) {
        exclude("com.mchange")
        exclude("com.zaxxer")
    }
}
