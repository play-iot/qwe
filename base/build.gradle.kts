plugins {
    `java-test-fixtures`
}

dependencies {
    api(JacksonLibs.core)
    api(JacksonLibs.databind)
    api(JacksonLibs.datetime)
    api(LogLibs.slf4j)
    api(UtilLibs.classgraph)
    api(VertxLibs.core)
    api(VertxLibs.rx2)
    api(ZeroLibs.utils)
    api(ZeroLibs.jpaExt)

    implementation(VertxLibs.config)

    compileOnly(VertxLibs.codegen)
    compileOnlyApi(JacksonLibs.annotations)

    testImplementation(VertxLibs.junit)
    testImplementation(VertxLibs.junit5)

    testFixturesApi(LogLibs.logback)
    testFixturesApi(TestLibs.junit5Api)
    testFixturesApi(TestLibs.junit5Engine)
    testFixturesApi(TestLibs.junit5Vintage)
    testFixturesApi(TestLibs.jsonAssert)
    testFixturesApi(VertxLibs.junit)
    testFixturesApi(VertxLibs.junit5)
    testFixturesCompileOnly(UtilLibs.lombok)
    testFixturesAnnotationProcessor(UtilLibs.lombok)
}
