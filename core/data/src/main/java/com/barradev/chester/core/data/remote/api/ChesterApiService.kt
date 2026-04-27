package com.barradev.chester.core.data.remote.api

import com.barradev.chester.core.data.remote.dto.CustomerDto
import com.barradev.chester.core.data.remote.dto.OrderDetailDto
import com.barradev.chester.core.data.remote.dto.OrdersDto
import com.barradev.chester.core.data.remote.dto.ProductDto
import com.barradev.chester.core.data.remote.dto.request.CreateOrderRequestDto
import com.barradev.chester.core.data.remote.dto.response.ConfirmCreateOrderResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChesterApiService {

    // --- CLIENTES ---
    @GET("api/customers/")
    suspend fun getCustomers(): Response<List<CustomerDto>>

    // --- PRODUCTOS ---
    @GET("api/products/products/")
    suspend fun getProducts(): Response<List<ProductDto>>


    // --- ÓRDENES ---

    // Historial de órdenes (Lectura)
    @GET("api/sales/orders/")
    suspend fun getOrders(): Response<List<OrdersDto>>

    // Obtener detalle de orden
    @GET("api/sales/orders/{id}/details/")
    suspend fun getOrderDetail(@Path("id") id: Long): List<OrderDetailDto>


    // CREAR ORDEN
    @POST("api/sales/orders/details/confirm/")
    suspend fun createOrderAndObtainId(
        @Body request: CreateOrderRequestDto
    ): Response<ConfirmCreateOrderResponseDto>

    @POST("api/sales/orders/{id}/close/")
    suspend fun createOrderClose(
        @Path("id") idOrder: Long
    ): Response<Unit>


}