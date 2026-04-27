package com.barradev.chester.core.data.di

import com.barradev.chester.core.data.remote.api.ChesterApiService
import com.barradev.chester.core.data.remote.api.FakeChesterApiServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideFakeChesterApiService(): ChesterApiService{
        return FakeChesterApiServiceImpl()
    }

    /* Implementacion real

    @Provides
    @Singleton
    fun provideChesterApiService(retrofit: Retrofit): ChesterApiService{
        return retrofit.create(ChesterApiService::class.java)
    }

    */

}