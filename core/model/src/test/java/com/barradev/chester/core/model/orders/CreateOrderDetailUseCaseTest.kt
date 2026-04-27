package com.barradev.chester.core.model.orders

import com.barradev.chester.core.model.models.Product
import com.barradev.chester.core.model.repository.OrderRepository
import com.barradev.chester.core.model.repository.ProductRepository
import com.barradev.chester.core.model.usecases.orders.CreateOrderDetailUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateOrderDetailUseCaseTest {

    private lateinit var orderRepository: OrderRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var useCase: CreateOrderDetailUseCase

    @BeforeEach
    fun setup() {
        orderRepository = mockk(relaxed = true)
        productRepository = mockk()

        useCase = CreateOrderDetailUseCase(orderRepository, productRepository)
    }


    @Test
    @DisplayName(
        """
        Given a valid productId,
        when the use case is invoked,
        then it should fetch the product and save the order detail correctly
    """
    )
    fun `given valid product, when invoked, then saves order detail`() = runTest {

        // Given
        val orderId = 10L
        val productId = 1L
        val mockProduct = Product(
            id = 100L,
            idRemote = 500L,
            name = "Product Test",
            price = 1500.0,
            currentStock = 10.0,
            imageUrl = "",
            category = 2
        )

        coEvery { productRepository.getProductById(productId) } returns mockProduct

        // When
        useCase(orderId, productId)

        // Then
        coVerify(exactly = 1) {
            orderRepository.createOrderDetail(withArg { detail ->
                assertEquals(orderId, detail.idLocalOrder)
                assertEquals(mockProduct.idRemote, detail.productRemoteId)
                assertEquals(mockProduct.name, detail.productName)
                assertEquals(mockProduct.price, detail.subTotal)
                assertEquals(1.0, detail.quantity)

            })
        }
    }



    @Test
    @DisplayName("""
        given a repository failure(databaseCorruption),
        when the use case is invoked,
        then it should propagate exception
    """)
    fun `given database error, when invoked, then throws exception`() = runTest {
        // Given
        val orderId = 10L
        val productId = 1L

        coEvery { productRepository.getProductById(any()) } throws RuntimeException("DB corrupted")

        //When y Then
        assertThrows<RuntimeException> {
            useCase(orderId,productId)
        }
        
    }

}