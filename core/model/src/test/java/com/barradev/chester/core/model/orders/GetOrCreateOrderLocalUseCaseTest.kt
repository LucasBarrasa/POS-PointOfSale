package com.barradev.chester.core.model.orders

import com.barradev.chester.core.model.repository.OrderRepository
import com.barradev.chester.core.model.usecases.orders.GetOrCreateOrderLocalUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GetOrCreateOrderLocalUseCaseTest {

    private lateinit var orderRepository: OrderRepository
    private lateinit var useCase: GetOrCreateOrderLocalUseCase

    @BeforeEach
    fun setUp() {
        orderRepository = mockk()
        useCase = GetOrCreateOrderLocalUseCase(orderRepository)
    }

    // --- ESCENARIOS CLIENTE RECURRENTE (CON ID) ---

    @Test
    @DisplayName("Cliente recurrente: Debe retornar el ID de la orden si ya existe un borrador")
    fun `given existing draft for recurring customer, when invoked, then return existing order id`() =
        runTest {
            // Given
            val idCustomer = 150L
            val existingOrderId = 99L
            coEvery { orderRepository.getOrderDraftByIdCustomer(idCustomer) } returns Result.success(
                existingOrderId
            )

            // When
            val result = useCase(null, null, idCustomer, "Nota", 0.0)

            // Then
            Assertions.assertTrue(result.isSuccess)
            Assertions.assertEquals(existingOrderId, result.getOrNull())
            coVerify(exactly = 0) { orderRepository.createNewOrder(any()) }
        }

    @Test
    @DisplayName("Cliente recurrente: Debe crear nueva orden si no existe borrador")
    fun `given no existing draft for recurring customer, when invoked, then create new order`() =
        runTest {
            // Given
            val idCustomer = 150L
            val newOrderId = 100L
            coEvery { orderRepository.getOrderDraftByIdCustomer(idCustomer) } returns Result.success(
                null
            )
            coEvery { orderRepository.createNewOrder(any()) } returns Result.success(newOrderId)

            // When
            val result = useCase(null, null, idCustomer, "Nota", 10.0)

            // Then
            Assertions.assertTrue(result.isSuccess)
            Assertions.assertEquals(newOrderId, result.getOrNull())
            coVerify(exactly = 1) { orderRepository.createNewOrder(any()) }
        }

    // --- ESCENARIOS CLIENTE CASUAL (SIN ID) ---

    @Test
    @DisplayName("Cliente casual: Debe crear orden exitosamente si provee nombre y dirección válidos")
    fun `given casual customer with valid name and address, when invoked, then create new order`() =
        runTest {
            // Given
            val newOrderId = 200L
            coEvery { orderRepository.createNewOrder(any()) } returns Result.success(newOrderId)

            // When
            val result = useCase("Juan Pérez", "Calle Falsa 123", null, "Venta rápida", 0.0)

            // Then
            Assertions.assertTrue(result.isSuccess)
            Assertions.assertEquals(newOrderId, result.getOrNull())
            // Verifica que no se llamó a la búsqueda por ID porque es un cliente casual
            coVerify(exactly = 0) { orderRepository.getOrderDraftByIdCustomer(any()) }
            coVerify(exactly = 1) { orderRepository.createNewOrder(any()) }
        }

    @Test
    @DisplayName("Cliente casual: Debe fallar si el nombre es nulo o vacío")
    fun `given casual customer with null or blank name, when invoked, then fail validation`() =
        runTest {
            // Given
            val invalidNames = listOf(null, "", "   ")

            for (invalidName in invalidNames) {
                // When
                val result = useCase(invalidName, "Calle Falsa 123", null, null, null)

                // Then
                Assertions.assertTrue(
                    result.isFailure,
                    "Debe rechazar la creación si el cliente casual no tiene nombre válido. Valor probado: '$invalidName'"
                )
                coVerify(exactly = 0) { orderRepository.createNewOrder(any()) }
            }
        }

    @Test
    @DisplayName("Cliente casual: Debe fallar si la dirección es nula o vacía")
    fun `given casual customer with null or blank address, when invoked, then fail validation`() =
        runTest {
            // Given
            val invalidAddresses = listOf(null, "", "   ")

            for (invalidAddress in invalidAddresses) {
                // When
                val result = useCase("Juan Pérez", invalidAddress, null, null, null)

                // Then
                Assertions.assertTrue(
                    result.isFailure,
                    "Debe rechazar la creación si el cliente casual no tiene dirección válida. Valor probado: '$invalidAddress'"
                )
                coVerify(exactly = 0) { orderRepository.createNewOrder(any()) }
            }
        }

    // --- ESCENARIOS DE FALLO DE INFRAESTRUCTURA (BASE DE DATOS) ---

    @Test
    @DisplayName("Infraestructura: Debe propagar error si falla lectura de base de datos local")
    fun `given repository read failure, when invoked, then return failure result`() = runTest {
        // Given
        val idCustomer = 150L
        val dbException = Exception("SQLiteDiskIOException: Database corrupted")
        coEvery { orderRepository.getOrderDraftByIdCustomer(idCustomer) } returns Result.failure(
            dbException
        )

        // When
        val result = useCase(null, null, idCustomer, "Nota", 0.0)

        // Then
        Assertions.assertTrue(result.isFailure)
        Assertions.assertEquals(dbException, result.exceptionOrNull())
        coVerify(exactly = 0) { orderRepository.createNewOrder(any()) }
    }

    @Test
    @DisplayName("Infraestructura: Debe propagar error si falla escritura de base de datos local")
    fun `given repository write failure, when invoked, then return failure result`() = runTest {
        // Given
        val idCustomer = 150L
        val dbException = Exception("SQLiteConstraintException: Disk full")
        coEvery { orderRepository.getOrderDraftByIdCustomer(idCustomer) } returns Result.success(
            null
        )
        coEvery { orderRepository.createNewOrder(any()) } returns Result.failure(dbException)

        // When
        val result = useCase(null, null, idCustomer, "Nota", 0.0)

        // Then
        Assertions.assertTrue(result.isFailure)
        Assertions.assertEquals(dbException, result.exceptionOrNull())
        coVerify(exactly = 1) { orderRepository.createNewOrder(any()) }
    }
}