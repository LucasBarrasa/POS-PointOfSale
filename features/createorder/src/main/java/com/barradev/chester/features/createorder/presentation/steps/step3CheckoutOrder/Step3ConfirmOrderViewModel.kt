package com.barradev.chester.features.createorder.presentation.steps.step3CheckoutOrder

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barradev.chester.core.model.models.OrderCompleteAggregate
import com.barradev.chester.core.model.repository.OrderRepository
import com.barradev.chester.core.model.usecases.orders.SubmitOrderUseCase
import com.barradev.chester.core.ui.presentation.formatter.CurrencyFormatter
import com.barradev.chester.navigation.Navigator
import com.barradev.chester.navigation.RootGraphDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class Step3ConfirmOrderViewModel @Inject constructor(
    private val navigator: Navigator,
    private val orderRepository: OrderRepository,
    private val submitOrderUseCase: SubmitOrderUseCase,
    private val currencyFormatter: CurrencyFormatter,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val orderId: Long = checkNotNull(savedStateHandle["orderId"])

    private val _uiEvent = Channel<Step3UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val uiState: StateFlow<Step3CheckoutOrderUiState> = orderRepository.getOrderDetailFlow(orderId)
        .filterNotNull()
        .map { aggregate ->
                mapOrderToUiState(aggregate)
        }
        .catch { error ->
            emit(Step3CheckoutOrderUiState.Error(error.message ?: "Error desconocido"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Step3CheckoutOrderUiState.Loading
        )

    fun onNavBack(){
        viewModelScope.launch {
            navigator.navigateBack()
        }
    }

    fun onConfirmOrder(){
        viewModelScope.launch {

            val result = submitOrderUseCase(orderId)

            result.fold(
                onSuccess = {
                    navigator.navigateToRoot(RootGraphDestinations.SellerMainRoute)
                },
                onFailure = { error ->
                    _uiEvent.send(Step3UiEvent.ShowSnackbar(error.message ?: "Error desconocido"))
                }
            )

        }
    }

    fun onDeleteItem(orderDetailId: Long){
        viewModelScope.launch {
            orderRepository.deleteOrderDetail(orderId, orderDetailId)
        }
    }

    private fun mapOrderToUiState(orderAggregate: OrderCompleteAggregate): Step3CheckoutOrderUiState{
        return Step3CheckoutOrderUiState.Success(
            clientName = orderAggregate.orderSummary.fullNameCustomer,
            clientAddress = orderAggregate.orderSummary.addressCustomer,
            totalItems = orderAggregate.orderSummary.totalItems,
            totalAmount = currencyFormatter.format(orderAggregate.orderSummary.totalAmount),
            itemsList = orderAggregate.detailsList.map { detail ->
                CheckoutItemUi(
                    idOrderDetail = detail.id,
                    nameProduct = detail.productName,
                    quantity = detail.quantity.toString(),
                    unitPrice = currencyFormatter.format(detail.productPrice),
                    subTotalPrice = currencyFormatter.format(detail.subTotal)
                )
            }
        )

    }

}