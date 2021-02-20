package io.github.zero88.qwe.gradle.app.task

import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property

@Suppress("UnstableApiUsage") open class SystemdServiceExtension(objects: ObjectFactory) {

    @Input
    val enabled = objects.property<Boolean>().convention(true)
    @Input
    val javaPath = objects.property<String>().convention("/usr/bin/java")
    @Input
    val arch = objects.property<Arch>()
    @Input
    val jvmProps = objects.listProperty<String>().empty()
    @Input
    val systemProps = objects.listProperty<String>().empty()
    @Input
    val serviceName = objects.property<String>()
    @Input
    val workingDir = objects.property<String>()
    @Input
    val configFile = objects.property<String>()
    @Input
    val params = objects.mapProperty<String, String>().empty()

    enum class Arch {
        X86_64, ARM_V6, ARM_V7, ARM_V8
    }
}
