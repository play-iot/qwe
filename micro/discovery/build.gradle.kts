dependencies {
    api(project(":micro:micro-config"))
    api(project(":micro:micro-metadata"))
    api(project(":micro:rpc"))
    api(project(":http:client"))
    api(VertxLibs.serviceDiscovery)

    compileOnly(VertxLibs.codegen)
    compileOnly(VertxLibs.codegen)
    compileOnly(VertxLibs.rx2)
    annotationProcessor(VertxLibs.codegen)

    testImplementation(VertxLibs.junit)
    testImplementation(testFixtures(project(":qwe-core")))
    testCompileOnly(VertxLibs.codegen)
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

