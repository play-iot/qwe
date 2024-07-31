import cloud.playio.gradle.generator.codegen.SourceSetName

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-test-fixtures`
    alias(libs.plugins.codegen)
}

codegen {
    vertx {
        version.set(libs.vertxCore.get().version)
        sources.addAll(arrayOf(SourceSetName.MAIN))
    }
}

dependencies {
    api(libs.vertxCore)
    api(libs.vertxAuth)
    api(libs.log4j2Api)
    api(libs.javaxAnnotation)
    api(libs.jacksonDatabind)
    api(libs.jacksonDatetime)
    api(libs.utils)
    api(libs.jpaExt)

    compileOnly(libs.vertxHazelcast)
    compileOnly(libs.vertxZookeeper)
    compileOnly(libs.vertxIgnite)
    compileOnly(libs.vertxInfinispan)

    codeGenerator(libs.vertxRx2)
    codeGenerator(libs.vertxRx3)
    compileOnlyApi(libs.jacksonAnnotations)

    testImplementation(libs.junitVertx)
    testImplementation(libs.junit5Vertx)
    testImplementation(libs.vertxRx2)
    testImplementation(libs.vertxJwt)
    testImplementation(libs.log4j2Core)

    testFixturesApi(testFixtures(libs.utils))
    testFixturesApi(libs.log4j2Core)
    testFixturesApi(libs.junit5Api)
    testFixturesApi(libs.junit5Engine)
    testFixturesApi(libs.junit5Vintage)
    testFixturesApi(libs.jsonAssert)
    testFixturesApi(libs.junitVertx)
    testFixturesApi(libs.junit5Vertx)
    testFixturesCompileOnly(libs.vertxRx2)
    testFixturesCompileOnly(libs.lombok)
    testFixturesCompileOnly(libs.jetbrainsAnnotations)
    testFixturesAnnotationProcessor(libs.lombok)
}
