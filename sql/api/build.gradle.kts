dependencies {
    api(project(":qwe-core"))
    api(project(":micro:micro-metadata"))
    api(project(":sql"))
    api(project(":sql:type"))
    compileOnly(VertxLibs.codegen)
}
