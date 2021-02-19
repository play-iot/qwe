package io.github.zero88.qwe.gradle

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property

@Suppress("UnstableApiUsage")
open class QWEExtension(objects: ObjectFactory) {

    companion object {

        const val NAME = "qwe"
        const val DEV_ID = "zero88"
        const val DEV_EMAIL = "sontt246@gmail.com"
    }

    /**
     * Project base name
     */
    val baseName = objects.property<String>()

    /**
     * Project title
     */
    val title = objects.property<String>()

    /**
     * Project description
     */
    val description = objects.property<String>()

    /**
     * Publishing info for Maven publication
     */
    val publishingInfo = PublishingInfo(objects)

    /**
     * Defines this project is executable or not
     */
    val application = objects.property<Boolean>()

    /**
     * Jar manifest
     */
    val manifest = objects.mapProperty<String, String>().convention(emptyMap())

    /**
     * Define developer is zero88
     */
    val zero88 = objects.property<Boolean>().convention(false)

    fun publishingInfo(configuration: Action<PublishingInfo>) {
        configuration.execute(publishingInfo)
    }
}
