package com.barradev.chester.core.model.repository

import com.barradev.chester.core.model.models.CreateNewOrder
import com.barradev.chester.core.model.models.OrderCompleteAggregate
import com.barradev.chester.core.model.models.OrderSummary
import com.barradev.chester.core.model.models.OrderDetail
import com.barradev.chester.core.model.models.RequestCreationOrder
import com.barradev.chester.core.model.models.SyncStatus
import kotlinx.coroutines.flow.Flow



interface OrderRepository {

    // Get
    fun getOrdersHistory(): Flow<List<OrderSummary>>

    suspend fun getOrderDetailByProduct(orderId: Long, productId: Long): OrderDetail?

    suspend fun getOrderAggregate(localOrderId: Long): Result<OrderCompleteAggregate>

    suspend fun getOrderDraftByIdCustomer(idCustomer: Long): Result<Long?>

    fun getOrderDetailFlow(localOrderId: Long): Flow<OrderCompleteAggregate?>

    suspend fun getOrderDetailQuantityById(orderDetailId: Long): Result<Double>


    // Create
    suspend fun createOrderDetail(orderDetail: OrderDetail) : Result<Long>

    suspend fun createNewOrder(newOrder: CreateNewOrder): Result<Long>

    suspend fun sendOrderRequest(requestCreationOrder: RequestCreationOrder): Result<Long>

    suspend fun confirmOrderRequest(remoteOrderId: Long): Result<Unit>

    suspend fun syncOrders(): Result<Unit>


    // Update
    suspend fun updateOrderDetail()

    suspend fun updateQuantityOrderDetail(orderId: Long, orderDetailId: Long, quantity: Double): Result<Unit>

    suspend fun updateSyncStatus(orderId: Long, status: SyncStatus): Result<Unit>

    suspend fun uploadOrder(orderId: Long): Result<Unit>


    //  Delete
    suspend fun deleteLocalOrder(orderId: Long)

    suspend fun deleteOrderDetail(orderId: Long, detailId: Long)


}