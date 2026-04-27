package com.barradev.chester.features.seller.presentation.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.barradev.chester.core.model.models.Product


@Composable
fun ProductsRoute(
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val listScrollState = rememberLazyGridState()

    LaunchedEffect(state.searchQuery) {
        if (listScrollState.firstVisibleItemIndex > 0) {
            listScrollState.scrollToItem(0)
        }
    }

    // Sincronizacion cada vez que se resume
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.syncProducts()
    }

    ProductsScreen(
        state = state,
        onSearchChange = viewModel::onSearchQueryChange,
        onProductClick = { /* Navegar a detalle del producto */ },
        listScrollState = listScrollState
    )

}

@Composable
fun ProductsScreen(
    state: ProductsUiState,
    onSearchChange: (String) -> Unit,
    onProductClick: (Long) -> Unit,
    listScrollState: LazyGridState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Catálogo",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Buscador
        TextField(
            value = state.searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar productos ...", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.Gray
                )
            },
            trailingIcon = {
                if (state.searchQuery.isNotBlank()) {
                    IconButton(
                        onClick = { onSearchChange("") }
                    ) {
                        Icon(
                            Icons.Outlined.CleaningServices,
                            contentDescription = "Limpiar texto",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            singleLine = true
        )

        // Indicador de sincronización
        if (state.isSyncing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(2.dp),
                color = Color(0xFFC48BFF),
                trackColor = Color.Transparent
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Listado de productos
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            state = listScrollState
        ) {
            items(state.products, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewProductScreen() {


    ProductsScreen(
        state = ProductsUiState(
            products = listOf(
                Product(
                    id = 1,
                    1,
                    name = "Producto 1",
                    price = 1500.toDouble(),
                    currentStock = 5.toDouble(),
                    imageUrl = "",
                    category = 1
                ),
                Product(
                    id = 2,
                    2,
                    name = "Producto 2",
                    price = 26000.toDouble(),
                    currentStock = 4.toDouble(),
                    imageUrl = "",
                    category = 1
                ),
                Product(
                    id = 3,
                    3,
                    name = "Producto 3",
                    price = 14500.toDouble(),
                    currentStock = 4.toDouble(),
                    imageUrl = "",
                    category = 2
                ),
            ),
            "",
            false
        ),
        onSearchChange = { },
        { },
        rememberLazyGridState()
    )
}


