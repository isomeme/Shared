package org.onereed.shared.tool

/**
 * Annotates shared classes and methods so that they will not be reported as being unused in any
 * particular project. This requires configuring Android Studio to recognize the annotation.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE) // Keeps it out of the final APK
annotation class SharedApi
