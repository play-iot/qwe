dependencies {
    api(VertxLibs.core)
    api(JacksonLibs.core)
    api(JacksonLibs.databind)
    api(JacksonLibs.datetime)
    api(LogLibs.slf4j)
    api(UtilLibs.classgraph)
    api(ZeroLibs.utils)
    api(ZeroLibs.jpaExt)
    api(VertxLibs.rx2)

    implementation(LogLibs.logback)
    implementation(VertxLibs.config)

    compileOnly(VertxLibs.codegen)
    compileOnlyApi(JacksonLibs.annotations)

    testImplementation(TestLibs.jsonAssert)
    testImplementation(TestLibs.junit)
    testImplementation(VertxLibs.junit)
}
