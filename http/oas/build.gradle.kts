dependencies {
    api(project(":micro:micro-metadata"))
    api(SwaggerLibs.swaggerCore)

    api("io.swagger.core.v3:swagger-jaxrs2:${SwaggerLibs.Version.swagger}")
    api("io.swagger.core.v3:swagger-jaxrs2-servlet-initializer-v2:${SwaggerLibs.Version.swagger}")
}
