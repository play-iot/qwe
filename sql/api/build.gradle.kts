dependencies {
    api(projects.core)
    api(project(":micro:micro-metadata"))
    api(project(":sql"))
    api(project(":sql:type"))
    compileOnly(VertxLibs.codegen)
}
