package com.barradev.chester.features.orderdetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.barradev.chester.core.model.models.OrderCompleteAggregate
import com.barradev.chester.core.model.repository.OrderRepository
import com.barradev.chester.core.ui.presentation.formatter.CurrencyFormatter
import com.barradev.chester.navigation.RootGraphDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.barradev.chester.navigation.Navigator

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository,
    private val currencyFormater: CurrencyFormatter,
    private val navigator: Navigator
) : ViewModel() {

    // Argumentos de navegacion
    val toRouteArgs = savedStateHandle.toRoute<RootGraphDestinations.OrderDetail>()
    val orderId = toRouteArgs.orderId

    private val _uiState = MutableStateFlow<OrderDetailUiState>(OrderDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()


    init {
        getDetails()
    }

    fun navToBack() {
        viewModelScope.launch {
            navigator.navigateBack()
        }
    }

    fun getDetails() {
        viewModelScope.launch {
            _uiState.value = OrderDetailUiState.Loading

            orderRepository.getOrderAggregate(localOrderId = orderId)
                .onSuccess { completeDetail ->

                    val uiStateMap = mapToUiState(completeDetail)

                    _uiState.value = uiStateMap
                }
                .onFailure { errorMessage ->
                    _uiState.value = OrderDetailUiState.Error(
                        errorMessage.message ?: "Ocurrio un error inesperado"
                    )
                }
        }

    }

    private fun mapToUiState(orderCompleteAggregate: OrderCompleteAggregate): OrderDetailUiState.Success {
        val summaryOrder = orderCompleteAggregate.orderSummary

        return OrderDetailUiState.Success(
            topBarTitle = summaryOrder.remoteOrderId.toString() ?: "",
            headerName = summaryOrder.fullNameCustomer,
            headerAddress = summaryOrder.addressCustomer,
            headerDate = summaryOrder.createdDate,
            headerItemsCount = summaryOrder.totalItems.toString(),
            headerTotalAmount = currencyFormater.format(summaryOrder.totalAmount),
            products = orderCompleteAggregate.detailsList.map { detail ->
                ProductDetailUi(
                    id = detail.id,
                    quantity = detail.quantity.toString(),
                    name = detail.productName,
                    unitPrice = currencyFormater.format(detail.productPrice),
                    subTotal = currencyFormater.format(detail.subTotal)
                )
            }
        )
    }

}