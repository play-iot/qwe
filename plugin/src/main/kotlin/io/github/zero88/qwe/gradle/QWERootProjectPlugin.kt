package io.github.zero88.qwe.gradle

import io.github.zero88.qwe.gradle.helper.prop
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestReport
import org.gradle.api.tasks.testing.TestResult
import org.gradle.kotlin.dsl.*
import org.gradle.testing.base.plugins.TestingBasePlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.File

class QWERootProjectPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (project != project.rootProject) {
            return
        }
        project.tasks {
            assembleTask(project)
            verificationTask(project)
        }
    }

    private fun TaskContainerScope.assembleTask(project: Project) {
        withType<Jar>().configureEach {
            onlyIf { project != project.rootProject }
        }
        withType<AbstractArchiveTask>().configureEach {
            onlyIf { project != project.rootProject }
        }
        register<Copy>("copySubProjectsArtifacts") {
            group = "distribution"
            dependsOn(project.subprojects.mapNotNull { it.tasks.findByName("assemble") })
            val zips = project.subprojects.fold(
                listOf<File>(),
                { r, p -> r.plus(p.buildDir.resolve("distributions")) })
            from(zips)
            into(project.buildDir.resolve("distributions"))
        }
        named("assemble") {
            finalizedBy("copySubProjectsArtifacts")
        }
    }

    private fun TaskContainerScope.verificationTask(project: Project) {
        val testFailures = mutableListOf<String>()
        register<Copy>("copySubProjectsTestResults") {
            group = "verification"
            dependsOn(project.subprojects.mapNotNull { it.tasks.findByName("test") })
            val zips = project.subprojects.fold(
                listOf<File>(),
                { r, p -> r.plus(p.buildDir.resolve(TestingBasePlugin.TEST_RESULTS_DIR_NAME)) })
            from(zips)
            into(project.buildDir.resolve(TestingBasePlugin.TEST_RESULTS_DIR_NAME))
        }
        register<TestReport>("testRootReport") {
            group = "verification"
            dependsOn("copySubProjectsTestResults")
            destinationDir = project.buildDir.resolve(TestingBasePlugin.TESTS_DIR_NAME)
            reportOn(project.subprojects.map { it.tasks.withType<Test>() })
            doLast {
                if (testFailures.isNotEmpty()) {
                    val failures = testFailures.joinToString("\n") { it }
                    val error =
                        "There were failing tests. See the report at: ${
                            destinationDir.toPath().resolve("index.html").toUri()
                        }\n${"-".repeat(50)}\n${failures}"
                    throw TaskExecutionException(project.tasks.withType<Test>().first(), RuntimeException(error))
                }
            }
        }
        named<Test>("test") {
            finalizedBy("testRootReport")
            ignoreFailures = true
            val handler =
                KotlinClosure2<TestDescriptor, TestResult, Any>(
                    { descriptor, result ->
                        if (descriptor.parent != null && result.resultType == TestResult.ResultType.FAILURE) {
                            testFailures.add("${descriptor.parent?.name} > ${descriptor.name} ${result.resultType}\n\t${result.exception}")
                        }
                    })
            project.subprojects.map {
                it.tasks.withType<Test> {
                    ignoreFailures = true
                    afterTest(handler)
                }
            }
        }
        register<JacocoReport>("jacocoRootReport") {
            group = "verification"
            dependsOn(project.subprojects.map { it.tasks.withType<Test>() })
            dependsOn(project.subprojects.map { it.tasks.withType<JacocoReport>() })
            additionalSourceDirs.setFrom(project.subprojects.map {
                it.convention.getPlugin<JavaPluginConvention>().sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).allSource.srcDirs
            })
            sourceDirectories.setFrom(project.subprojects.map {
                it.convention.getPlugin<JavaPluginConvention>().sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).allSource.srcDirs
            })
            classDirectories.setFrom(project.subprojects.map {
                it.convention.getPlugin<JavaPluginConvention>().sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).output
            })
            executionData.setFrom(project.fileTree(".") {
                include("**/build/jacoco/test.exec")
            })
            reports {
                csv.isEnabled = false
                xml.isEnabled = true
                xml.destination = project.layout.buildDirectory.file("reports/jacoco/coverage.xml").get().asFile
                html.isEnabled = prop(project, "jacocoHtml", "true").toBoolean()
                html.destination = project.layout.buildDirectory.file("reports/jacoco/html").get().asFile
            }
        }
    }

}
