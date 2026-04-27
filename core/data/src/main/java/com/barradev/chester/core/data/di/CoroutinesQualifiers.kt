package com.barradev.chester.core.data.di

import javax.inject.Qualifier


@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatchers

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatchers

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatchers
