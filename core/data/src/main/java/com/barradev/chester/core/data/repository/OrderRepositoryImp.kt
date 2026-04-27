package com.barradev.chester.core.data.repository

import com.barradev.chester.core.data.di.IoDispatchers
import com.barradev.chester.core.data.local.dao.OrderDao
import com.barradev.chester.core.data.local.entity.OrderDetailEntity
import com.barradev.chester.core.data.local.entity.OrderEntity
import com.barradev.chester.core.data.mapper.toDomain
import com.barradev.chester.core.data.mapper.toDto
import com.barradev.chester.core.data.mapper.toEntity
import com.barradev.chester.core.data.remote.api.ChesterApiService
import com.barradev.chester.core.data.remote.dto.response.ErrorResponseDto
import com.barradev.chester.core.model.models.CreateNewOrder
import com.barradev.chester.core.model.models.OrderCompleteAggregate
import com.barradev.chester.core.model.models.OrderDetail
import com.barradev.chester.core.model.models.OrderDetailRequest
import com.barradev.chester.core.model.models.OrderSummary
import com.barradev.chester.core.model.models.ProductRequest
import com.barradev.chester.core.model.models.RequestCreationOrder
import com.barradev.chester.core.model.models.SyncStatus
import com.barradev.chester.core.model.repository.OrderRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class OrderRepositoryImp @Inject constructor(
    private val orderDao: OrderDao,
    private val api: ChesterApiService,
    private val jsonParser: Json,
    @param:IoDispatchers private val ioDispatchers: CoroutineDispatcher
) : OrderRepository {

    override fun getOrdersHistory(): Flow<List<OrderSummary>> {
        return orderDao.getOrderSummary().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getOrderDetailByProduct(
        orderId: Long, productId: Long
    ): OrderDetail? {
        return orderDao.getProductOrderDetail(orderId, productId)?.toDomain()
    }

    override suspend fun getOrderAggregate(localOrderId: Long): Result<OrderCompleteAggregate> {
        return withContext(ioDispatchers) {
            runCatching {

                val entityAggregate = orderDao.getOrderAggregate(localOrderId)
                    ?: throw IllegalStateException("Orden no encontrada en BD local para ID: $localOrderId")


                if (entityAggregate.details.isNotEmpty() || entityAggregate.order.remoteId == null) {
                    return@runCatching entityAggregate.toDomain()
                }

                val remoteId = requireNotNull(entityAggregate.order.remoteId) {
                    "Inconsistencia de datos. La orden no tiene ID asignado en el servidor"
                }

                val remoteOrderDetailsDto = api.getOrderDetail(remoteId)

                val orderDetailsToEntities = remoteOrderDetailsDto.map {
                    it.toEntity(
                        entityAggregate.order.localId
                    )
                }

                orderDao.insertOrderDetails(orderDetailsToEntities)

                val actualizedOrdersAggregate = orderDao.getOrderAggregate(localOrderId)
                    ?: throw IllegalStateException("Error critico, no se encuentra la orden en la BD")

                actualizedOrdersAggregate.toDomain()
            }
        }
    }

    override suspend fun getOrderDraftByIdCustomer(idCustomer: Long): Result<Long?> {
        return withContext(ioDispatchers) {
            try {
                val idOrderFiltered = orderDao.getDraftOrderIdByCustomerId(idCustomer)

                Result.success(idOrderFiltered)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun getOrderDetailFlow(localOrderId: Long): Flow<OrderCompleteAggregate?> {
        return orderDao.getOrderAggregateFlow(localOrderId).map { aggregate ->

            aggregate?.toDomain()
        }

    }

    override suspend fun getOrderDetailQuantityById(orderDetailId: Long): Result<Double> {
        return runCatching {
            orderDao.getOrderDetailQuantityById(orderDetailId)
        }
    }


    override suspend fun createOrderDetail(
        orderDetail: OrderDetail,
    ): Result<Long> {
        return withContext(ioDispatchers) {
            try {

                val productOrderDetailEntity = OrderDetailEntity(
                    remoteId = null,
                    orderLocalId = orderDetail.idLocalOrder,
                    productRemoteId = orderDetail.productRemoteId,
                    productName = orderDetail.productName,
                    productPrice = orderDetail.productPrice,
                    quantity = orderDetail.quantity,
                    discount = orderDetail.discount,
                    subtotal = orderDetail.subTotal
                )

                val idProductDetail =
                    orderDao.createOrderDetailAndUpdateAmount(productOrderDetailEntity)

                Result.success(idProductDetail)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    }

    @OptIn(ExperimentalTime::class)
    override suspend fun createNewOrder(newOrder: CreateNewOrder): Result<Long> {
        return withContext(ioDispatchers) {
            runCatching {
                val orderEntity = OrderEntity(
                    localId = 0,
                    remoteId = null,
                    createdDate = LocalDateTime.now().toString(),
                    customerName = newOrder.casualCustomerAddress,
                    address = newOrder.casualCustomerAddress,
                    amount = null,
                    note = newOrder.note,
                    pdf = null,
                    customerId = newOrder.idCustomer,
                    discount = null,
                    syncStatus = SyncStatus.DRAFT
                )

                orderDao.insertOneOrder(orderEntity)
            }
        }

    }

    override suspend fun sendOrderRequest(requestCreationOrder: RequestCreationOrder): Result<Long> {
        return withContext(ioDispatchers) {
            try {
                val request = requestCreationOrder.toDto()

                val response = api.createOrderAndObtainId(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(body.order.id)
                    } else {
                        Result.failure(Exception("La respuesta del servidor vino vacía."))
                    }
                } else {

                    // JSON del error
                    val errorBodyString = response.errorBody()?.string()

                    val errorMessage = try {
                        if (!errorBodyString.isNullOrBlank()) {
                            val errorDto =
                                jsonParser.decodeFromString<ErrorResponseDto>(errorBodyString)

                            if (errorDto.errorMessages.isNotEmpty()) {
                                errorDto.errorMessages.joinToString(separator = "\n") { "- $it" }
                            } else {
                                "Error sin mensaje específico del servidor."
                            }
                        } else {
                            "Error del servidor (${response.code()}) sin cuerpo de respuesta."
                        }
                    } catch (e: Exception) {
                        "Error al procesar la solicitud: ${response.message()} (Código: ${response.code()})"
                    }

                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(Exception("La respuesta del servidor vino vacía."))
            }
        }
    }

    override suspend fun confirmOrderRequest(remoteOrderId: Long): Result<Unit> {
        return withContext(ioDispatchers) {
            try {
                val response = api.createOrderClose(remoteOrderId)

                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error Desconocido"
                    Result.failure(Exception("Error ${response.code()} $errorMsg"))
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    override suspend fun syncOrders(): Result<Unit> {
        return withContext(ioDispatchers) {
            runCatching {
                val responseOrders = api.getOrders().body() ?: emptyList()

                val ordersEntities = responseOrders.map { it.toEntity(SyncStatus.SYNCED) }

                orderDao.upsertRemoteOrders(ordersEntities)
            }
        }
    }

    override suspend fun updateOrderDetail() {}


    override suspend fun updateQuantityOrderDetail(
        orderId: Long, orderDetailId: Long, quantity: Double
    ): Result<Unit> {
        return runCatching {
            orderDao.updateQuantityOrderDetailAndAmounts(
                orderId = orderId, orderDetailId = orderDetailId, quantity = quantity
            )
        }
    }

    override suspend fun updateSyncStatus(orderId: Long, status: SyncStatus): Result<Unit> {
        return try {
            orderDao.updateSyncStatus(orderId, status)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadOrder(orderId: Long): Result<Unit> {
        return try {
            val orderComplete = orderDao.getOrderAggregate(orderId)
                ?: return Result.failure(Exception("Error en la BD. No se encuentra la orden"))

            if (orderComplete.details.isEmpty()) {
                return Result.failure(Exception("No puedes subir una orden vacia"))
            }

            updateSyncStatus(orderId, SyncStatus.SYNCING)

            // Mapeo a DTOs
            val requestDto = RequestCreationOrder(
                orderComplete.details.map {
                    OrderDetailRequest(
                        remoteProduct = ProductRequest(it.productRemoteId),
                        quantity = it.quantity
                    )
                }
            )

            val remoteId = sendOrderRequest(requestDto)
            val confirmResult = confirmOrderRequest(remoteId.getOrThrow())

            if (!confirmResult.isSuccess) {
                throw Exception("Error al confirmar la orden en el servidor.")
            }

            orderDao.updateRemoteIdAndStatus(orderId, remoteId.getOrThrow(), SyncStatus.SYNCED)
            syncOrders()

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            updateSyncStatus(orderId, SyncStatus.SYNC_ERROR)
            Result.failure(e)
        }
    }


    // Borrado Orden Local
    override suspend fun deleteLocalOrder(orderId: Long) {
        runCatching {
            orderDao.deleteOrderById(orderId)
        }
    }

    override suspend fun deleteOrderDetail(orderId: Long, detailId: Long) {
        orderDao.deleteOrderDetailAndSyncOrder(orderId, detailId)
    }

}