package io.github.zero88.qwe.gradle

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property

open class QWEExtension(objects: ObjectFactory) {

    val baseName = objects.property<String>()
    val title = objects.property<String>()
    val description = objects.property<String>()
    val publishingInfo = PublishingInfo(objects)

    fun publishingInfo(configuration: Action<PublishingInfo>) {
        configuration.execute(publishingInfo)
    }
}
