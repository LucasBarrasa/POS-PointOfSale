package com.barradev.chester.core.model.orders

import com.barradev.chester.core.model.repository.OrderRepository
import com.barradev.chester.core.model.usecases.orders.DeleteOrderDetailUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteOrderDetailUseCaseTest {

    private lateinit var orderRepository: OrderRepository
    private lateinit var useCase: DeleteOrderDetailUseCase


    @BeforeEach
    fun setup(){
        orderRepository = mockk()
        useCase = DeleteOrderDetailUseCase(orderRepository)
    }



    @Test
    @DisplayName("""
        Given valid order and detail IDs, 
        when the use case is invoked, 
        then it should delete the order detail in the repository
    """)
    fun `given valid ids, when invoked, then deletes order detail`() = runTest {
        // Given
        val orderId = 10L
        val orderDetail = 100L

        coEvery { orderRepository.deleteOrderDetail(orderId, orderDetail) } just runs

        // When
        useCase(orderId, orderDetail)


        // Then
        coVerify(exactly = 1) {
            orderRepository.deleteOrderDetail(orderId, orderDetail)
        }

    }

    @Test
    @DisplayName("""
        Given a database error,
        when the use case is invoked,
        then it should propagate the exception
    """)
    fun `given database error, when invoked, then throws exception`() = runTest {
        // Given
        val orderId = 10L
        val orderDetail = 100L

        coEvery { orderRepository.deleteOrderDetail(any(), any()) } throws RuntimeException("DB Error")

        // When & Then
        assertThrows<RuntimeException> {
            useCase(orderId,orderDetail)
        }

    }

}