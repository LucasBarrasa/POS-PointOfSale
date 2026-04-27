package com.barradev.chester.core.data.local.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.barradev.chester.core.data.local.entity.OrderDetailEntity
import com.barradev.chester.core.data.local.entity.OrderEntity
import com.barradev.chester.core.data.local.relation.OrderSummaryTuple
import com.barradev.chester.core.data.local.relation.OrderAggregate
import com.barradev.chester.core.model.models.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OrderDao {

    // GET
    @Transaction
    @Query(
        """
        SELECT
            o.*,
            COALESCE(c.first_name || COALESCE(' ' || c.last_name , ''), o.customer_name, 'Cliente desconocido') as resolved_customer_name,
            COALESCE(c.address, o.address, 'Sin direccion registrada') as resolved_customer_address,
            (SELECT TOTAL(quantity) FROM order_details od WHERE od.order_local_id = o.id) as items_count,
            (SELECT TOTAL(quantity * product_price) FROM order_details od WHERE od.order_local_id = o.id) as computed_total
        FROM orders o 
        LEFT JOIN customers c ON o.customer_id = c.remote_id 
        ORDER BY o.created_date DESC, o.id DESC
    """
    )
    abstract fun getOrderSummary(): Flow<List<OrderSummaryTuple>>

    @Transaction
    @Query(
        """
    SELECT
        o.*, 
        -- Resoluciones de cliente
        COALESCE(c.first_name || COALESCE('' || c.last_name , ''), o.customer_name, 'Cliente desconocido') as resolved_customer_name,
        COALESCE(c.address, o.address, 'Sin direccion registrada') as resolved_customer_address,
        
        -- Cantidad de items
        (SELECT TOTAL(quantity) FROM order_details od WHERE od.order_local_id = o.id) as items_count,

        (SELECT TOTAL(quantity * product_price) FROM order_details od WHERE od.order_local_id = o.id) as computed_total

    FROM orders o 
    LEFT JOIN customers c ON o.customer_id = c.remote_id 
    WHERE o.id = :orderId
    """
    )
    abstract fun getOrderSummaryById(orderId: Long): Flow<OrderSummaryTuple?>

    @Transaction // Obligatorio porque OrderAggregate usa @Relation
    @Query("SELECT * FROM orders WHERE id = :orderId")
    abstract fun getOrderAggregateFlow(orderId: Long): Flow<OrderAggregate?>

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId")
    abstract suspend fun getOrderAggregate(orderId: Long): OrderAggregate?

    @Transaction
    @Query("SELECT * FROM orders WHERE remote_id = :remoteOrderId LIMIT 1")
    abstract suspend fun getOrderByRemoteId(remoteOrderId: Long): OrderEntity?

    @Query("SELECT id FROM orders WHERE customer_id = :idCustomer AND sync_status = 'DRAFT' LIMIT 1")
    abstract suspend fun getDraftOrderIdByCustomerId(idCustomer: Long): Long?

    @Query("SELECT * FROM order_details WHERE order_local_id = :orderId AND product_remote_id = :productId LIMIT 1")
    abstract suspend fun getProductOrderDetail(orderId: Long, productId: Long): OrderDetailEntity?

    @Query("SELECT quantity FROM order_details WHERE id = :idOrderDetail")
    abstract suspend fun getOrderDetailQuantityById(idOrderDetail: Long) : Double


    //  CREATE

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertOneOrder(newOrder: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOrderDetails(orderDetails: List<OrderDetailEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun createOrderDetail(orderDetail: OrderDetailEntity): Long

    @Transaction
    open suspend fun createOrderDetailAndUpdateAmount(orderDetail: OrderDetailEntity): Long{
        val id = createOrderDetail(orderDetail)

        val realTotal = calculateOrderTotal(orderDetail.orderLocalId)
        updateAmountOrder(orderDetail.orderLocalId, realTotal)
        return id
    }


    //  UPDATE

    @Query("UPDATE orders SET sync_status = :status WHERE id = :orderId")
    abstract suspend fun updateSyncStatus(orderId: Long, status: SyncStatus)


    @Query("UPDATE orders SET remote_id = :remoteId, sync_status = :status WHERE id = :orderId")
    abstract suspend fun updateRemoteIdAndStatus(orderId: Long, remoteId: Long, status: SyncStatus)

    @Update
    abstract suspend fun updateOneOrder(orderToUpdate: OrderEntity)

    @Query("UPDATE orders SET amount = :newTotalAmount WHERE id = :orderId")
     abstract suspend fun updateAmountOrder(orderId: Long, newTotalAmount: Double)

    @Transaction
    open suspend fun upsertRemoteOrders(remoteOrders: List<OrderEntity>) {
        remoteOrders.forEach { remoteOrderEntity ->

            val existingOrder = remoteOrderEntity.remoteId?.let { getOrderByRemoteId(it) }

            if (existingOrder != null) {
                val orderToUpdate = remoteOrderEntity.copy(localId = existingOrder.localId)
                updateOneOrder(orderToUpdate)
            } else {
                insertOneOrder(remoteOrderEntity)
            }
        }
    }


    // Update order detail quantity and amount
    @Query("""
        UPDATE order_details 
        SET quantity = :quantity, 
                subtotal = :quantity * product_price 
        WHERE id = :orderDetailId
    """)
    abstract suspend fun updateOrderDetailQuantity(quantity: Double, orderDetailId: Long)

    @Query("SELECT COALESCE(quantity * product_price, 0.0) FROM order_details WHERE id = :orderDetailId")
    abstract suspend fun calculateOrderDetailAmount(orderDetailId: Long): Double

    @Query("SELECT COALESCE(SUM(subtotal), 0.0) FROM order_details WHERE order_local_id = :orderId")
    abstract suspend fun calculateOrderTotal(orderId: Long): Double

    @Transaction
    // Actualizar cantidad de detalle de orden
    // monto de detalle
    // monto orden completo
    open suspend fun updateQuantityOrderDetailAndAmounts (orderId: Long, orderDetailId: Long, quantity: Double){
        updateOrderDetailQuantity(quantity, orderDetailId)

        val newOrderAmount = calculateOrderTotal(orderId)
        updateAmountOrder(orderId, newOrderAmount)
    }


    //  DELETE

    @Query("DELETE FROM orders WHERE id = :idOrder")
    abstract suspend fun deleteOrderById(idOrder: Long)

    @Query("DELETE FROM order_details WHERE id = :detailId")
    abstract suspend fun deleteOrderDetail(detailId: Long)

    @Transaction
    open suspend fun deleteOrderDetailAndSyncOrder(localOrderId: Long, detailId: Long){
        deleteOrderDetail(detailId)
        val newTotal = calculateOrderTotal(localOrderId)
        updateAmountOrder(localOrderId, newTotal)
    }


}