dependencies {
    api(project(":qwe-core"))
    implementation(UtilLibs.quartz) {
        exclude("com.mchange")
        exclude("com.zaxxer")
    }
}
