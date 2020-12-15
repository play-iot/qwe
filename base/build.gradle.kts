plugins {
    `java-test-fixtures`
}

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

    implementation(VertxLibs.config)

    compileOnly(VertxLibs.codegen)
    compileOnlyApi(JacksonLibs.annotations)

    testImplementation(TestLibs.jsonAssert)
    testImplementation(TestLibs.junit)
    testImplementation(VertxLibs.junit)
    testImplementation(VertxLibs.junit5)

    testFixturesApi(TestLibs.junit5Api)
    testFixturesApi(TestLibs.jsonAssert)
    testFixturesApi(TestLibs.junit)
    testFixturesApi(VertxLibs.junit)
    testFixturesApi(LogLibs.logback)
    testFixturesCompileOnly(UtilLibs.lombok)
    testFixturesAnnotationProcessor(UtilLibs.lombok)
}
