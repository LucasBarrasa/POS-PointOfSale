package com.barradev.chester.features.seller.presentation.products

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barradev.chester.core.model.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.syncProducts()
        }
    }

    // Estado del buscador
    private val _searchQuery = MutableStateFlow("")

    // Estado de sincronización (loading)
    private val _isSyncing = MutableStateFlow(false)

    // Combinamos la fuente de verdad (DB) con el input del usuario
    val uiState: StateFlow<ProductsUiState> = combine(
        repository.getProducts(),
        _searchQuery,
        _isSyncing
    ) { products, query, syncing ->

        // Lógica de filtrado: Nombre contiene Query (insensible a mayúsculas)
        val filteredProducts = if (query.isBlank()) {
            products
        } else {
            products.filter { product ->
                product.name.contains(query, ignoreCase = true)
            }
        }

        ProductsUiState(
            products = filteredProducts,
            searchQuery = query,
            isSyncing = syncing
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductsUiState()
    )

    // Syncronizacion al entrar a la pantalla
    fun syncProducts() {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.syncProducts()
            _isSyncing.value = false
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
}