dependencies {
    api(project(":qwe-core"))

    compileOnly(VertxLibs.codegen)
    compileOnly(VertxLibs.rx2)
    annotationProcessor(VertxLibs.codegen)

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":qwe-core")))
}
