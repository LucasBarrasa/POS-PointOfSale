package com.barradev.chester.core.data.di

import com.barradev.chester.core.data.repository.CustomerRepositoryImpl
import com.barradev.chester.core.data.repository.OrderRepositoryImp
import com.barradev.chester.core.data.repository.ProductRepositoryImpl
import com.barradev.chester.core.model.repository.CustomerRepository
import com.barradev.chester.core.model.repository.OrderRepository
import com.barradev.chester.core.model.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class SellerDataModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository (
        impl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCustomerRepository(
        impl: CustomerRepositoryImpl
    ): CustomerRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        impl: OrderRepositoryImp
    ): OrderRepository
}