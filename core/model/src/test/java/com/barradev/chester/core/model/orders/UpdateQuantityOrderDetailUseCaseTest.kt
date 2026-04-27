package com.barradev.chester.core.model.orders

import com.barradev.chester.core.model.models.QuantityAddProduct
import com.barradev.chester.core.model.repository.OrderRepository
import com.barradev.chester.core.model.usecases.orders.UpdateQuantityOrderDetailUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateQuantityOrderDetailUseCaseTest {

    private lateinit var orderRepository: OrderRepository
    private lateinit var useCase: UpdateQuantityOrderDetailUseCase

    @BeforeEach
    fun setUp() {
        orderRepository = mockk()
        useCase = UpdateQuantityOrderDetailUseCase(orderRepository)
    }

    @Test
    fun `given valid Delta, when current quantity exists, then updates repository with sum`() = runTest {
        // Given
        val orderId = 1L
        val detailId = 100L
        val currentQuantity = 2.0
        val deltaToAdd = 3.0
        val expectedQuantity = 5.0

        coEvery { orderRepository.getOrderDetailQuantityById(detailId) } returns Result.success(currentQuantity)
        coEvery { orderRepository.updateQuantityOrderDetail(orderId, detailId, expectedQuantity) } returns Result.success(Unit)

        // When
        val result = useCase(orderId, detailId, QuantityAddProduct.Delta(deltaToAdd))

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { orderRepository.updateQuantityOrderDetail(orderId, detailId, expectedQuantity) }
    }

    @Test
    fun `given negative Delta resulting in less than 1, when calculated, then coerces to minimum of 1`() = runTest {
        // Given
        val orderId = 1L
        val detailId = 100L
        val currentQuantity = 2.0
        val deltaToSubtract = -5.0 // Esto daría -3.0
        val expectedQuantity = 1.0 // Debe ser forzado a 1.0 por la regla de negocio

        coEvery { orderRepository.getOrderDetailQuantityById(detailId) } returns Result.success(currentQuantity)
        coEvery { orderRepository.updateQuantityOrderDetail(orderId, detailId, expectedQuantity) } returns Result.success(Unit)

        // When
        val result = useCase(orderId, detailId, QuantityAddProduct.Delta(deltaToSubtract))

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { orderRepository.updateQuantityOrderDetail(orderId, detailId, expectedQuantity) }
    }

    @Test
    fun `given valid SetSpecific quantity, when invoked, then updates repository directly`() = runTest {
        // Given
        val orderId = 1L
        val detailId = 100L
        val specificQuantity = 15.0

        coEvery { orderRepository.updateQuantityOrderDetail(orderId, detailId, specificQuantity) } returns Result.success(Unit)

        // When
        val result = useCase(orderId, detailId, QuantityAddProduct.SetSpecific(specificQuantity))

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { orderRepository.updateQuantityOrderDetail(orderId, detailId, specificQuantity) }
        coVerify(exactly = 0) { orderRepository.getOrderDetailQuantityById(any()) } // Verifica que no hit a la BD por el current
    }

    @Test
    fun `given negative SetSpecific quantity, when invoked, then updates repository with invalid data EXPOSING BUG`() = runTest {
        // Given
        val orderId = 1L
        val detailId = 100L
        val invalidSpecificQuantity = -10.0 // Esto no debería permitirse

        coEvery { orderRepository.updateQuantityOrderDetail(orderId, detailId, invalidSpecificQuantity) } returns Result.success(Unit)

        // When
        val result = useCase(orderId, detailId, QuantityAddProduct.SetSpecific(invalidSpecificQuantity))

        // Then
        // ESTE TEST PASA con tu código actual, pero demuestra que tu lógica permite corromper la BD offline.
        // Tienes que arreglar el UseCase para que este comportamiento cambie y falle.
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { orderRepository.updateQuantityOrderDetail(orderId, detailId, invalidSpecificQuantity) }
    }

    @Test
    fun `given missing local data, when get current quantity fails, then returns failure and stops execution`() = runTest {
        // Given
        val orderId = 1L
        val detailId = 100L
        val expectedError = Exception("Row not found in local SQLite DB")

        coEvery { orderRepository.getOrderDetailQuantityById(detailId) } returns Result.failure(expectedError)

        // When
        val result = useCase(orderId, detailId, QuantityAddProduct.Delta(1.0))

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedError, result.exceptionOrNull())
        coVerify(exactly = 0) { orderRepository.updateQuantityOrderDetail(any(), any(), any()) }
    }

    @Test
    fun `given local db exception during update, when attempting to save, then propagates failure`() = runTest {
        // Given
        val orderId = 1L
        val detailId = 100L
        val specificQuantity = 5.0
        val expectedError = Exception("Disk full or constraints failed in Room")

        coEvery { orderRepository.updateQuantityOrderDetail(orderId, detailId, specificQuantity) } returns Result.failure(expectedError)

        // When
        val result = useCase(orderId, detailId, QuantityAddProduct.SetSpecific(specificQuantity))

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedError, result.exceptionOrNull())
    }
}