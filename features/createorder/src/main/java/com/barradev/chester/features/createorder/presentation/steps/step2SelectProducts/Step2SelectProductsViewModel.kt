package com.barradev.chester.features.createorder.presentation.steps.step2SelectProducts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barradev.chester.core.model.models.QuantityAddProduct
import com.barradev.chester.core.model.repository.OrderRepository
import com.barradev.chester.core.model.repository.ProductRepository
import com.barradev.chester.core.model.usecases.orders.CreateOrderDetailUseCase
import com.barradev.chester.core.model.usecases.orders.DeleteOrderDetailUseCase
import com.barradev.chester.core.model.usecases.orders.UpdateQuantityOrderDetailUseCase
import com.barradev.chester.features.createorder.presentation.CreateOrderDestinations
import com.barradev.chester.navigation.Navigator
import com.barradev.chester.navigation.RootGraphDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class Step2SelectProductsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val navigator: Navigator,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val upsertProductOrderUseCase: UpdateQuantityOrderDetailUseCase,
    private val createOrderDetailUseCase: CreateOrderDetailUseCase,
    private val deleteOrderDetailUseCase: DeleteOrderDetailUseCase
) : ViewModel() {


    private val currentOrderId: Long = checkNotNull(savedStateHandle["orderId"])

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


    @OptIn(FlowPreview::class)
    private val debounceSearchQuery = _searchQuery
        .debounce(300L)
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)


    val uiState: StateFlow<Step2SelectProductsUiState> = combine(
        productRepository.getProducts(),
        orderRepository.getOrderDetailFlow(currentOrderId),
        debounceSearchQuery
    ) { products, orderAggregate, query ->

        val detailsMap = orderAggregate?.detailsList?.associateBy { it.productRemoteId } ?: emptyMap()

        val productsFiltered = products
            .filter { it.name.contains(query, ignoreCase = true) }
            .map { product ->

                val detail = detailsMap[product.idRemote]

                ProductItemUiState(
                    id = product.id,
                    orderDetailId = detail?.id,
                    name = product.name,
                    price = product.price,
                    currentStock = product.currentStock,
                    quantityInOrder = detail?.quantity ?: 0.0
                )
            }


        Step2SelectProductsUiState(
            productsList = productsFiltered,
            searchQuery = query,
            totalItemsCount = orderAggregate?.orderSummary?.totalItems ?: 0.0,
            totalAmount = orderAggregate?.orderSummary?.totalAmount ?: 0.0
        )

    }.flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Step2SelectProductsUiState()
        )


    // --- ACCIONES ---

    fun onQuerySearchChange(newQuery: String) {
        _searchQuery.value = newQuery
    }


    fun onAddProduct(productId: Long) {
        viewModelScope.launch {
            createOrderDetailUseCase(
                orderId = currentOrderId,
                productId = productId
            )
        }
    }


    fun onAddOneQuantity(orderDetailId: Long?) {
        onChangeQuantity(
            orderDetailId,
            1.0
        )
    }

    fun onRestQuantity(orderDetailId: Long?) {
        onChangeQuantity(
            orderDetailId,
            -1.0
        )
    }

    private fun onChangeQuantity(orderDetailId: Long?, quantity: Double) {
        viewModelScope.launch {
            if (orderDetailId != null) {
                upsertProductOrderUseCase(
                    orderId = currentOrderId,
                    orderDetailId = orderDetailId,
                    quantityType = QuantityAddProduct.Delta(quantity)
                )
            }
        }
    }

    fun onDeleteLocalOrder(orderDetailId: Long) {
        viewModelScope.launch {
            deleteOrderDetailUseCase(currentOrderId, orderDetailId)
        }
    }

    fun onNavigateBack() {
        viewModelScope.launch {
            navigator.navigateBack()
        }
    }

    fun onNavigateToHome() {
        viewModelScope.launch {
            navigator.navigateTo(
                RootGraphDestinations.SellerMainRoute,
                RootGraphDestinations.SellerMainRoute,
                true
            )
        }
    }

    fun onNavigateToConfirmOrder() {
        viewModelScope.launch {
            navigator.navigateTo(CreateOrderDestinations.Step3ConfirmOrder(currentOrderId))
        }
    }

}