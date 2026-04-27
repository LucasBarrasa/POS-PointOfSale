package com.barradev.chester.core.model.orders

import com.barradev.chester.core.model.models.OrderCompleteAggregate
import com.barradev.chester.core.model.models.OrderDetail
import com.barradev.chester.core.model.models.OrderSummary
import com.barradev.chester.core.model.models.SyncStatus
import com.barradev.chester.core.model.repository.OrderRepository
import com.barradev.chester.core.model.usecases.orders.SubmitOrderUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SubmitOrderUseCaseTest {
    private lateinit var orderRepository: OrderRepository
    private lateinit var useCase: SubmitOrderUseCase


    @BeforeEach
    fun setup() {
        orderRepository = mockk()
        useCase = SubmitOrderUseCase(orderRepository)
    }


    @Test
    @DisplayName("Given a valid order ID, when the use case is invoked, then it should submit the order")
    fun `given a valid order, when invoked, then update Sync status, submit the order and returns success`() =
        runTest {
            // Given
            val orderId = 100L
            val validOrder = OrderCompleteAggregate(
                orderSummary = OrderSummary(
                    localId = 100L,
                    remoteOrderId = 101,
                    fullNameCustomer = "Test Name",
                    addressCustomer = "Test Address",
                    createdDate = "01-01-2026",
                    note = "Test nota",
                    pdf = "test Pdf",
                    discount = 0.0,
                    totalAmount = 1500.00,
                    totalItems = 1.0,
                    status = SyncStatus.DRAFT
                ),
                detailsList = listOf(
                    OrderDetail(
                        idLocalOrder = 100L,
                        productRemoteId = 1L,
                        productName = "Prueba nombre",
                        productPrice = 1500.00,
                        quantity = 1.0,
                        discount = 0.0,
                        subTotal = 1500.00
                    )
                )
            )

            coEvery { orderRepository.getOrderAggregate(orderId) } returns Result.success(validOrder)
            coEvery {
                orderRepository.updateSyncStatus(
                    orderId = orderId,
                    status = SyncStatus.SYNCING
                )
            } returns Result.success(Unit)
            coEvery { orderRepository.uploadOrder(orderId) } returns Result.success(Unit)

            // When
            val result = useCase(orderId)

            // Then
            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { orderRepository.updateSyncStatus(orderId, SyncStatus.SYNCING) }
            coVerify(exactly = 1) { orderRepository.uploadOrder(orderId) }

        }


    // Empty order
    @DisplayName("Given a empty order, when the use case is invoked, then it should return failure")
    @Test
    fun `given a empty order, when invoked, then returns failure`() = runTest {
        // Given
        val orderId = 10L
        val emptyOrder = OrderCompleteAggregate(
            orderSummary = OrderSummary(
                localId = 100L,
                remoteOrderId = 101,
                fullNameCustomer = "Test Name",
                addressCustomer = "Test Address",
                createdDate = "01-01-2026",
                note = "Test nota",
                pdf = "test Pdf",
                discount = 0.0,
                totalAmount = 1500.00,
                totalItems = 1.0,
                status = SyncStatus.DRAFT
            ),
            detailsList = listOf()
        )

        coEvery { orderRepository.getOrderAggregate(orderId) } returns Result.success(emptyOrder)

        // When
        val result = useCase(orderId)

        // Then
        assertTrue { result.isFailure }
        assertEquals("No puedes enviar una orden vacia", result.exceptionOrNull()?.message)

        coVerify(exactly = 0) { orderRepository.updateSyncStatus(any(), any()) }
        coVerify(exactly = 0) { orderRepository.uploadOrder(any()) }
    }

    // Failure Database
    @Test
    fun `given a failure to retrieve order from database, when invoked, then returns failure immediately`() =
        runTest {
            // Given
            val orderId = 1L
            val databaseError = Exception("Room Error")
            coEvery { orderRepository.getOrderAggregate(orderId) } returns Result.failure(
                databaseError
            )

            // When
            val result = useCase(orderId)

            // Then
            assertTrue(result.isFailure)
            assertEquals(databaseError, result.exceptionOrNull())
            coVerify(exactly = 0) { orderRepository.updateSyncStatus(any(), any()) }
            coVerify(exactly = 0) { orderRepository.uploadOrder(any()) }
        }

    // Failure Update status
    @Test
    fun `given a failure updating sync status, when invoked, then aborts upload and returns failure`() =
        runTest {
            // Given
            val orderId = 1L
            val validOrder = OrderCompleteAggregate(
                orderSummary = OrderSummary(
                    localId = 100L,
                    remoteOrderId = 101,
                    fullNameCustomer = "Test Name",
                    addressCustomer = "Test Address",
                    createdDate = "01-01-2026",
                    note = "Test nota",
                    pdf = "test Pdf",
                    discount = 0.0,
                    totalAmount = 1500.00,
                    totalItems = 1.0,
                    status = SyncStatus.DRAFT
                ),
                detailsList = listOf(
                    OrderDetail(
                        idLocalOrder = 100L,
                        productRemoteId = 1L,
                        productName = "Prueba nombre",
                        productPrice = 1500.00,
                        quantity = 1.0,
                        discount = 0.0,
                        subTotal = 1500.00
                    )
                )
            )
            val statusError = Exception("Error al actualizar el estado de la orden")

            coEvery { orderRepository.getOrderAggregate(orderId) } returns Result.success(validOrder)
            coEvery {
                orderRepository.updateSyncStatus(
                    orderId,
                    SyncStatus.SYNCING
                )
            } returns Result.failure(statusError)

            // When
            val result = useCase(orderId)

            // Then
            assertTrue(result.isFailure)
            assertEquals(
                "Error al actualizar el estado de la orden",
                result.exceptionOrNull()?.message
            )
            coVerify(exactly = 0) { orderRepository.uploadOrder(any()) }
        }

    // Failure upload order
    @Test
    fun `given a failure during upload order, when invoked, then the use case returns failure`() =
        runTest {
            // Given
            val orderId = 1L
            val validOrder = OrderCompleteAggregate(
                orderSummary = OrderSummary(
                    localId = 100L,
                    remoteOrderId = 101,
                    fullNameCustomer = "Test Name",
                    addressCustomer = "Test Address",
                    createdDate = "01-01-2026",
                    note = "Test nota",
                    pdf = "test Pdf",
                    discount = 0.0,
                    totalAmount = 1500.00,
                    totalItems = 1.0,
                    status = SyncStatus.DRAFT
                ),
                detailsList = listOf(
                    OrderDetail(
                        idLocalOrder = 100L,
                        productRemoteId = 1L,
                        productName = "Prueba nombre",
                        productPrice = 1500.00,
                        quantity = 1.0,
                        discount = 0.0,
                        subTotal = 1500.00
                    )
                )
            )
            val uploadError =
                Exception("Se agotó el tiempo de espera de la red o falló la restricción de la base de datos durante la carga.")

            coEvery { orderRepository.getOrderAggregate(orderId) } returns Result.success(validOrder)
            coEvery {
                orderRepository.updateSyncStatus(
                    orderId,
                    SyncStatus.SYNCING
                )
            } returns Result.success(Unit)
            coEvery { orderRepository.uploadOrder(orderId) } returns Result.failure(uploadError)

            // When
            val result = useCase(orderId)

            // Then
            assertTrue(result.isFailure, "El UseCase debe fallar si uploadOrder falla")
            assertEquals(uploadError, result.exceptionOrNull())
        }

}