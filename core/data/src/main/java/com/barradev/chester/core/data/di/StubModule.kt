package com.barradev.chester.core.data.di

import com.barradev.chester.core.data.repository.FakeSessionManagerImpl
import com.barradev.chester.core.model.repository.SessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class StubModule {

    @Binds
    @Singleton
    abstract fun bindSessionManager(
        fakeImpl: FakeSessionManagerImpl
    ): SessionManager

}