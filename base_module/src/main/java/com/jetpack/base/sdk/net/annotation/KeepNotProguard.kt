package com.jetpack.base.sdk.net.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by ZhouMeng on 2018/9/23.
 * 可配置类，方法，属性，配置后，将不会被混淆
 */

@Retention(RetentionPolicy.CLASS)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FIELD
)
annotation class KeepNotProguard
