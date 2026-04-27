package com.barradev.chester.features.seller.presentation.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.barradev.chester.core.model.models.Customer
import com.barradev.chester.features.seller.presentation.products.ProductCard
import com.barradev.chester.features.seller.presentation.products.ProductsUiState
import com.barradev.chester.features.seller.presentation.products.ProductsViewModel


@Composable
fun CustomerRoute(
    viewModel: CustomersViewModel = hiltViewModel()
) {
    val state by viewModel.stateUi.collectAsStateWithLifecycle()

    // Sincronizar CADA VEZ que se entra a esta pantalla
    LaunchedEffect(Unit) {
        viewModel.syncCustomer()
    }

    CustomersScreen(
        state = state,
        onSearchChange = viewModel::onSearchQueryChange,
        onCustomerClick = {}
    )

}

@Composable
fun CustomersScreen(
    state: CustomerSellerUiState,
    onSearchChange: (String) -> Unit,
    onCustomerClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Clientes",
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
            placeholder = { Text("Buscar cliente ...", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.Gray
                )
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

        // Grid
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = state.customers,
                key = { it.id }
            ) { customer ->
                CustomerCard(
                    customer = customer,
                    onClick = { onCustomerClick(customer.id) }
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewCustomerScreen() {

    val listCustomer = listOf<Customer>(
        Customer(
            id = 1,
            remoteId = 1,
            fullName = "Distribuidora El Trebol S.A.",
            address = "Av. San Martín 1234, Dolores",
            phone = "2241-555555",
            hasCurrentAccount = true,
            isActive = true
        ),
        Customer(
            id = 2,
            remoteId = 2,
            fullName = "Distribuidora El Trebol S.A.",
            address = "Av. San Martín 1234, Dolores",
            phone = "2241-555555",
            hasCurrentAccount = true,
            isActive = true
        ),
    )
    val state = CustomerSellerUiState(
        listCustomer,
        "",
        false
    )

    MaterialTheme {
        CustomersScreen(
            state = state,
            onSearchChange = {},
            onCustomerClick = {}
        )
    }


}