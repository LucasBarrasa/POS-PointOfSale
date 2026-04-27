package com.barradev.chester.features.createorder.presentation.steps.step1SelectClient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.barradev.chester.core.model.models.Customer


@Composable
fun Step1SelectClientRoute(
    viewModel: Step1SelectClientViewModel = hiltViewModel()
) {
    val stateUi by viewModel.stateUi.collectAsStateWithLifecycle()

    Step1SelectClientScreen(
        state = stateUi,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onNavigateToSelectProducts = viewModel::navigateToSelectProducts,
        onNavigateBack = viewModel::onNavigateBack
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1SelectClientScreen(
    state: Step1SelectClientUiState,
    onNavigateToSelectProducts: (Long) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onNavigateBack:()-> Unit
) {

    Scaffold(
        modifier = Modifier.background(Color(0xFF121212)),
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = "Selecciona un cliente",
                    style = MaterialTheme.typography.titleLarge
                )
            }, navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }
            })
        }) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
                .fillMaxSize()
        ) {

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {Text("Buscar Cliente ...", color = Color.Gray)},
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                shape = RoundedCornerShape(30.dp),
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

            Spacer(Modifier.padding(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = state.customerList,
                    key = { it.id }
                ) { customer ->
                    CustomerCard(
                        customer = customer,
                        onClick = {onNavigateToSelectProducts(customer.remoteId)},
                    )

                }
            }
        }
    }
}

@Composable
fun CustomerCard(
    customer: Customer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val cardContainerColor = MaterialTheme.colorScheme.surfaceContainerLow

    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = cardContainerColor
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = customer.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = customer.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        }

    }


}

@Preview
@Composable
fun PreviewCustomerCard() {

    MaterialTheme {
        CustomerCard(
            Customer(
                id = 1,
                remoteId = 1,
                fullName = "Lo de pepito",
                address = "Av. Siempre viva 123",
                phone = "2245-555555",
                hasCurrentAccount = false,
                isActive = true
            ),
            onClick = {},
        )
    }
}


@Preview(
    showSystemUi = true,
    showBackground = true,
)
@Composable
fun PreviewStep1SelectClientScreen() {

    val fakeListCustomer = listOf<Customer>(
        Customer(
            id = 1,
            remoteId = 1,
            fullName = "Lo de pepito",
            address = "Av. Siempre viva 123",
            phone = "2245-555555",
            hasCurrentAccount = false,
            isActive = true
        ), Customer(
            id = 2,
            remoteId = 2,
            fullName = "Prueba 123",
            address = "Calle falsa",
            phone = "2245-555555",
            hasCurrentAccount = false,
            isActive = true
        )
    )

    val fakeStateUI = Step1SelectClientUiState(
        customerList = fakeListCustomer,
    )

    Step1SelectClientScreen(
        state = fakeStateUI,
        onSearchQueryChange = {},
        onNavigateToSelectProducts = {},
        onNavigateBack = {}
    )
}