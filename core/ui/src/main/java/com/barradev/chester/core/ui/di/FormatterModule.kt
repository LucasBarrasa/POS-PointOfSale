package com.barradev.chester.core.ui.di

import com.barradev.chester.core.ui.presentation.formatter.CurrencyFormatter
import com.barradev.chester.core.ui.presentation.formatter.CurrencyFormatterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class FormatterModule {

    @Binds
    @Singleton
    abstract fun bindCurrencyFormatter(impl: CurrencyFormatterImpl): CurrencyFormatter
}