package com.barradev.chester.features.seller.presentation.home

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barradev.chester.core.model.models.OrderSummary
import com.barradev.chester.core.model.models.SyncStatus
import com.barradev.chester.core.model.repository.OrderRepository
import com.barradev.chester.core.model.repository.ProductRepository
import com.barradev.chester.core.ui.presentation.formatter.CurrencyFormatter
import com.barradev.chester.features.seller.presentation.home.model.OrderUiModel
import com.barradev.chester.navigation.Navigator
import com.barradev.chester.navigation.RootGraphDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class HomeSellerViewModel @Inject constructor(
    private val navigator: Navigator,
    private val orderRepository: OrderRepository,
    private val currencyFormatter: CurrencyFormatter
) : ViewModel() {

    val uiState: StateFlow<HomeSellerStateUi> = orderRepository.getOrdersHistory()
        .map<List<OrderSummary>, HomeSellerStateUi> { listOrders ->

             val uiModels = listOrders.map{ it.toUiModel() }

            HomeSellerStateUi.Success(uiModels)
        }
        .catch { error ->
            emit(HomeSellerStateUi.Error(error.message ?: "Error desconocido"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeSellerStateUi.Loading
        )


    fun onNavToCreateOrder (){
        viewModelScope.launch {
            navigator.navigateTo(RootGraphDestinations.CreateOrderRoute())
        }
    }

    fun onNavToOrderDetail(idOrder: Long){
        viewModelScope.launch {
            navigator.navigateTo(RootGraphDestinations.OrderDetail(idOrder))
        }
    }

    fun syncOrders() {
        viewModelScope.launch {
            orderRepository.syncOrders()
        }
    }

    private fun OrderSummary.toUiModel(): OrderUiModel {
        // Lógica de presentación de colores
        val (color, text) = when (this.status) {
            SyncStatus.PENDING_SYNC -> Color(0xFFFF9800) to "Pendiente" // Naranja
            SyncStatus.SYNCED -> Color(0xFF4CAF50) to "Sincronizado"    // Verde
            SyncStatus.SYNC_ERROR -> Color(0xFFF44336) to "Error"       // Rojo
            else -> Color.Gray to "Borrador"
        }

        return OrderUiModel(
            id = this.localId,
            title = this.fullNameCustomer,
            date = this.createdDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            itemsCount = this.totalItems,
            totalAmount = currencyFormatter.format(this.totalAmount) ,
            statusLabel = text,
            statusColor = color,
            paymentMethod = "No especifica"
        )
    }

}