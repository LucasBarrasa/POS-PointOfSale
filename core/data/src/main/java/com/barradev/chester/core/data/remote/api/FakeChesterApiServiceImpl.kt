package com.barradev.chester.core.data.remote.api

import com.barradev.chester.core.data.remote.dto.CustomerDto
import com.barradev.chester.core.data.remote.dto.OrderDetailDto
import com.barradev.chester.core.data.remote.dto.OrdersDto
import com.barradev.chester.core.data.remote.dto.ProductDto
import com.barradev.chester.core.data.remote.dto.request.CreateOrderRequestDto
import com.barradev.chester.core.data.remote.dto.response.ConfirmCreateOrderResponseDto
import com.barradev.chester.core.data.remote.dto.response.OrderCreatedDto
import kotlinx.coroutines.delay
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject

class FakeChesterApiServiceImpl @Inject constructor() : ChesterApiService {

    // Simulador latencia de red
    private val networkDelay = 1500L

    // Fake Mocks
    val mockCustomers = listOf(
        CustomerDto(
            idRemote = 101L,
            state = true, // Asumiendo true = Activo
            createDate = "2024-01-10T08:30:00Z",
            modifiedDate = "2025-02-15T10:00:00Z",
            deleteDate = null,
            firstName = "Julián",
            lastName = "Rossi",
            address = "Av. Corrientes 1234, CABA",
            phone = "+541144445555",
            comment = "Cliente VIP con prioridad de entrega",
            currentAccount = true // Asumiendo true = Saldo acreedor/activo
        ), CustomerDto(
            idRemote = 102L,
            state = false,
            createDate = "2023-11-20T14:20:00Z",
            modifiedDate = "2024-12-01T09:15:00Z",
            deleteDate = "2024-12-01T09:15:00Z",
            firstName = "Mariana",
            lastName = "Paz",
            address = "Calle 50 #678, La Plata",
            phone = "+542216778899",
            comment = "Cuenta cerrada por inactividad",
            currentAccount = false
        ), CustomerDto(
            idRemote = 103L,
            state = true,
            createDate = "2025-01-05T11:00:00Z",
            modifiedDate = "2025-03-20T16:45:00Z",
            deleteDate = null,
            firstName = "Roberto",
            lastName = "Gómez",
            address = "Bv. Oroño 456, Rosario",
            phone = "+543415551234",
            comment = null,
            currentAccount = true
        ), CustomerDto(
            idRemote = 104L,
            state = true,
            createDate = "2024-06-15T09:00:00Z",
            modifiedDate = "2025-01-10T12:00:00Z",
            deleteDate = null,
            firstName = "Lucía",
            lastName = "Fernández",
            address = "General Paz 99, Córdoba",
            phone = null,
            comment = "Contactar solo por email",
            currentAccount = false
        ), CustomerDto(
            idRemote = 105L,
            state = true,
            createDate = "2025-02-01T08:00:00Z",
            modifiedDate = "2025-02-01T08:00:00Z",
            deleteDate = null,
            firstName = "Esteban",
            lastName = "Quito",
            address = null,
            phone = "+542944112233",
            comment = "Pendiente completar dirección",
            currentAccount = true
        ), CustomerDto(
            idRemote = 106L,
            state = false,
            createDate = "2023-05-12T10:30:00Z",
            modifiedDate = "2024-03-15T11:00:00Z",
            deleteDate = null,
            firstName = "Sofía",
            lastName = "Méndez",
            address = "España 234, Mendoza",
            phone = "+542614567890",
            comment = "Cliente moroso",
            currentAccount = false
        ), CustomerDto(
            idRemote = 107L,
            state = true,
            createDate = "2025-03-01T15:00:00Z",
            modifiedDate = "2025-03-10T10:00:00Z",
            deleteDate = null,
            firstName = "Ricardo",
            lastName = null,
            address = "San Martín 10, Salta",
            phone = "+543875123456",
            comment = "Nueva alta de sistema",
            currentAccount = true
        ), CustomerDto(
            idRemote = 108L,
            state = true,
            createDate = "2024-12-24T20:00:00Z",
            modifiedDate = "2024-12-24T20:00:00Z",
            deleteDate = null,
            firstName = "Hilda",
            lastName = "Sosa",
            address = "Belgrano 888, Tucumán",
            phone = null,
            comment = null,
            currentAccount = true
        ), CustomerDto(
            idRemote = 109L,
            state = true,
            createDate = "2025-01-20T13:45:00Z",
            modifiedDate = "2025-03-25T09:00:00Z",
            deleteDate = null,
            firstName = "Iván",
            lastName = "Drago",
            address = "Libertador 5500, CABA",
            phone = "+541133221100",
            comment = "Requiere facturación especial",
            currentAccount = true
        ), CustomerDto(
            idRemote = 110L,
            state = false,
            createDate = "2024-08-10T12:00:00Z",
            modifiedDate = "2024-10-10T12:00:00Z",
            deleteDate = "2024-10-10T12:00:00Z",
            firstName = "Julia",
            lastName = "Russo",
            address = "Alvear 12, Posadas",
            phone = "+543764556677",
            comment = "Baja solicitada por el usuario",
            currentAccount = false
        )
    )

    val mockProducts = listOf(
        ProductDto(
            idRemote = 1L,
            name = "Coca Cola 1.5L",
            price = 2500.0,
            stock = 50.0,
            stockControl = true,
            imageUrl = "https://cdn.link/coke.png",
            categoryId = 1,
            modifiedDate = "2026-03-01T10:00:00Z",
            deletedDate = null
        ), ProductDto(
            idRemote = 2L,
            name = "Pan de Molde",
            price = 1800.0,
            stock = 20.0,
            stockControl = true,
            imageUrl = null,
            categoryId = 2,
            modifiedDate = "2026-03-05T12:00:00Z",
            deletedDate = null
        ), ProductDto(
            idRemote = 3L,
            name = "Jabón Líquido",
            price = 3200.0,
            stock = 15.0,
            stockControl = true,
            imageUrl = "https://cdn.link/soap.png",
            categoryId = 3,
            modifiedDate = "2026-03-10T15:30:00Z",
            deletedDate = null
        ), ProductDto(
            idRemote = 4L,
            name = "Papas Fritas 200g",
            price = 1500.0,
            stock = 100.0,
            stockControl = false,
            imageUrl = null,
            categoryId = 1,
            modifiedDate = "2026-03-20T09:00:00Z",
            deletedDate = null
        )
    )

    val mockOrders = listOf(
        OrdersDto(
            id = 501L,
            createdDate = "2026-03-25T10:00:00Z",
            customerName = "Julián Rossi",
            amount = 6800.0,
            address = "Av. Corrientes 1234",
            note = "Entregar en portería",
            pdf = "https://server.com/invoice_501.pdf",
            customerId = 101L,
            discount = 500.0
        ), OrdersDto(
            id = 502L,
            createdDate = "2026-03-25T11:30:00Z",
            customerName = "Lucía Fernández",
            amount = 1500.0,
            address = "General Paz 99",
            note = null,
            pdf = null,
            customerId = 104L,
            discount = 0.0
        ), OrdersDto(
            id = 503L,
            createdDate = "2026-03-25T14:15:00Z",
            customerName = "Roberto Gómez",
            amount = 5000.0,
            address = "Bv. Oroño 456",
            note = "Tocar timbre fuerte",
            pdf = "https://server.com/invoice_503.pdf",
            customerId = 103L,
            discount = 200.0
        )
    )

    val mockOrderDetails = listOf(
        // Detalles para la Orden 501 (Julián Rossi)
        OrderDetailDto(
            id = 1001L,
            orderId = 501L,
            product = mockProducts[0],
            state = true,
            createdDate = "2026-03-25T10:00:00Z",
            modifiedDate = "2026-03-25T10:00:00Z",
            productPrice = 2500.0,
            quantity = 2.0,
            discount = 0.0 // Total 5000
        ), OrderDetailDto(
            id = 1002L,
            orderId = 501L,
            product = mockProducts[1],
            state = true,
            createdDate = "2026-03-25T10:01:00Z",
            modifiedDate = "2026-03-25T10:01:00Z",
            productPrice = 1800.0,
            quantity = 1.0,
            discount = 0.0 // Total 1800 (Suma 6800 ok)
        ),

        // Detalle para la Orden 502 (Lucía Fernández)
        OrderDetailDto(
            id = 1003L,
            orderId = 502L,
            product = mockProducts[3],
            state = true,
            createdDate = "2026-03-25T11:30:00Z",
            modifiedDate = "2026-03-25T11:30:00Z",
            productPrice = 1500.0,
            quantity = 1.0,
            discount = 0.0
        ),

        // Detalles para la Orden 503 (Roberto Gómez)
        OrderDetailDto(
            id = 1004L,
            orderId = 503L,
            product = mockProducts[1],
            state = true,
            createdDate = "2026-03-25T14:15:00Z",
            modifiedDate = "2026-03-25T14:15:00Z",
            productPrice = 1800.0,
            quantity = 1.0,
            discount = 0.0
        ), OrderDetailDto(
            id = 1005L,
            orderId = 503L,
            product = mockProducts[2],
            state = true,
            createdDate = "2026-03-25T14:16:00Z",
            modifiedDate = "2026-03-25T14:16:00Z",
            productPrice = 3200.0,
            quantity = 1.0,
            discount = 0.0 // Total 5000 ok
        )
    )


    // Clients
    override suspend fun getCustomers(): Response<List<CustomerDto>> {
        delay(networkDelay)
        return Response.success(mockCustomers)
    }


    // Products
    override suspend fun getProducts(): Response<List<ProductDto>> {
        delay(networkDelay)
        return Response.success(mockProducts)
    }


    // Orders
    override suspend fun getOrders(): Response<List<OrdersDto>> {
        delay(networkDelay)
        return Response.success(mockOrders)
    }

    override suspend fun getOrderDetail(id: Long): List<OrderDetailDto> {
        delay(networkDelay)
        return mockOrderDetails.filter { detailOrder ->
            detailOrder.orderId == id
        }
    }

    override suspend fun createOrderAndObtainId(
        request: CreateOrderRequestDto
    ): Response<ConfirmCreateOrderResponseDto> {
        delay(networkDelay)

        // Lógica Mock: Si la orden no tiene detalles, simulamos un error de validación
        if (request.detail.isEmpty()) {
            return Response.error(400, "La orden debe contener al menos un producto".toResponseBody())
        }

        // Simulamos que el servidor genera un ID incremental o aleatorio
        val generatedOrderId = (1000..9999).random().toLong()

        val mockResponse = ConfirmCreateOrderResponseDto(
            message = "Orden creada exitosamente en el servidor",
            order = OrderCreatedDto(id = generatedOrderId)
        )

        return Response.success(mockResponse)
    }

    override suspend fun createOrderClose(idOrder: Long): Response<Unit> {
        delay(networkDelay)

        // Simulamos una regla de negocio: si el ID es negativo, falla.
        if (idOrder <= 0) {
            return Response.error(404, "ID de orden no encontrado".toResponseBody())
        }

        // Devolvemos éxito con Unit (equivalente a HTTP 204 No Content o 200 OK vacío)
        return Response.success(Unit)
    }
}