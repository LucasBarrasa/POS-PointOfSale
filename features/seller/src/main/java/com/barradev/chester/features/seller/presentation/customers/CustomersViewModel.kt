package com.barradev.chester.features.seller.presentation.customers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barradev.chester.core.model.repository.CustomerRepository
import com.barradev.chester.features.seller.presentation.products.ProductsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val repositoryCustomer: CustomerRepository
) : ViewModel() {


    init {
        viewModelScope.launch {
            repositoryCustomer.syncCustomers()
        }
    }

    private val searchQuery = MutableStateFlow("")

    private val _isSyncing = MutableStateFlow(false)

    val stateUi: StateFlow<CustomerSellerUiState> = combine(
        repositoryCustomer.getCustomers(),
        searchQuery,
        _isSyncing
    ) { customers, query, syncing ->

        val filteredCustomer = if (query.isBlank()) {
            customers
        } else {
            customers.filter { customer ->
                customer.fullName.contains(query, true)
            }
        }

        CustomerSellerUiState(
            filteredCustomer,
            query,
            syncing
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CustomerSellerUiState()
    )

    fun syncCustomer(){
        viewModelScope.launch {
            _isSyncing.value = true
            repositoryCustomer.syncCustomers()
            _isSyncing.value = false
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery.value = newQuery
    }
}