package com.barradev.chester.features.createorder.presentation.steps.step1SelectClient

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barradev.chester.core.model.repository.CustomerRepository
import com.barradev.chester.core.model.usecases.orders.GetOrCreateOrderLocalUseCase
import com.barradev.chester.features.createorder.presentation.CreateOrderDestinations
import com.barradev.chester.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class Step1SelectClientViewModel @Inject constructor(
    private val navigator: Navigator,
    private val customerRepository: CustomerRepository,
    private val getOrCreateOrderLocalUseCase: GetOrCreateOrderLocalUseCase
): ViewModel(){

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()


    val stateUi: StateFlow<Step1SelectClientUiState> = combine(
        customerRepository.getCustomers(),
        searchQuery,
        isSyncing
    ){ customers, searchQuery, isSyncing ->

        val customersFiltered = if (searchQuery.isBlank()){
            customers
        }else{
            customers.filter { it.fullName.contains(searchQuery,true) }
        }

        Step1SelectClientUiState(
            customersFiltered,
            searchQuery,
            isSyncing
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Step1SelectClientUiState()

    )


    fun onSearchQueryChange(newQuery: String){
        _searchQuery.value = newQuery
    }

    fun onNavigateBack(){
        viewModelScope.launch {
            navigator.navigateBack()
        }
    }

    fun navigateToSelectProducts(idCustomer: Long){
        viewModelScope.launch {


            getOrCreateOrderLocalUseCase(
                casualName = null,
                casualAddress = null,
                idCustomer = idCustomer,
                note = null,
                discount = null
            ).fold(
                onSuccess = { orderId ->

                    navigator.navigateTo(CreateOrderDestinations.Step2SelectProducts(
                        orderId = orderId
                    ))
                },
                onFailure = { error ->
                }
            )

        }
    } 

}