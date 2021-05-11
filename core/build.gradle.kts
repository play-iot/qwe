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
    api(ZeroLibs.utils)
    api(ZeroLibs.jpaExt)

    implementation(VertxLibs.config)

    compileOnly(VertxLibs.codegen)
    compileOnly(VertxLibs.rx2)
    compileOnly(UtilLibs.jetbrainsAnnotations)
    compileOnlyApi(JacksonLibs.annotations)
    annotationProcessor(VertxLibs.codegen)

    testImplementation(VertxLibs.junit)
    testImplementation(VertxLibs.junit5)
    testImplementation(VertxLibs.rx2)

    testFixturesApi(LogLibs.logback)
    testFixturesApi(TestLibs.junit5Api)
    testFixturesApi(TestLibs.junit5Engine)
    testFixturesApi(TestLibs.junit5Vintage)
    testFixturesApi(TestLibs.jsonAssert)
    testFixturesApi(VertxLibs.junit)
    testFixturesApi(VertxLibs.junit5)
    testFixturesCompileOnly(VertxLibs.rx2)
    testFixturesCompileOnly(UtilLibs.lombok)
    testFixturesAnnotationProcessor(UtilLibs.lombok)
}

tasks.register<JavaCompile>("annotationProcessing") {
    group = "other"
    source = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).java
    destinationDir = project.file("${project.buildDir}/generated/main/java")
    classpath = configurations.compileClasspath.get()
    options.annotationProcessorPath = configurations.compileClasspath.get()
    options.compilerArgs = listOf(
        "-proc:only",
        "-processor", "io.vertx.codegen.CodeGenProcessor",
        "-Acodegen.output=${project.projectDir}/src/main"
    )
}

tasks.compileJava {
    dependsOn(tasks.named("annotationProcessing"))
}

sourceSets {
    main {
        java {
            srcDirs(project.file("${project.buildDir}/generated/main/java"))
        }
    }
}

