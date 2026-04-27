package com.barradev.chester.features.createorder.presentation.steps.step3CheckoutOrder

// Archivo: Step3ConfirmOrderScreen.kt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun Step3ConfirmOrderRoute(
    viewModel: Step3ConfirmOrderViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is Step3UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }

    }

    Step3ConfirmOrderScreen(
        state = state,
        onBack = viewModel::onNavBack,
        onConfirmOrder = viewModel::onConfirmOrder,
        onDeleteProduct = viewModel::onDeleteItem,
        snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step3ConfirmOrderScreen(
    state: Step3CheckoutOrderUiState,
    onBack: () -> Unit,
    onConfirmOrder: () -> Unit,
    onDeleteProduct: (Long) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Resumen del Pedido") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            if (state is Step3CheckoutOrderUiState.Success) {
                StickyCheckoutBottomBar(
                    totalAmount = state.totalAmount,
                    totalItems = state.totalItems,
                    onConfirm = onConfirmOrder
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is Step3CheckoutOrderUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is Step3CheckoutOrderUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onBack) { Text("Volver") }
                    }
                }

                is Step3CheckoutOrderUiState.Success -> {
                    CheckoutSuccessContent(
                        state = state,
                        onDeleteProduct = onDeleteProduct
                    )
                }
            }
        }
    }
}


@Composable
private fun CheckoutSuccessContent(
    state: Step3CheckoutOrderUiState.Success,
    onDeleteProduct: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tarjeta Cliente
        item {
            CustomerInfoSection(state.clientName, state.clientAddress)
        }

        // Título Productos
        item {
            Text(
                text = "Productos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Lista Productos
        items(state.itemsList, key = { it.idOrderDetail }) { item ->
            ProductRowItem(
                name = item.nameProduct,
                qtyDescription = "${item.quantity} u. - ${item.unitPrice}",
                subTotal = item.subTotalPrice,
                onDelete = { onDeleteProduct(item.idOrderDetail) }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        }

        // Espacio final extra por seguridad visual
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}


@Composable
fun CustomerInfoSection(name: String, address: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(
                alpha = 0.6f
            )
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            if (address.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 32.dp)
                )
            }
        }
    }
}

@Composable
fun ProductRowItem(name: String, qtyDescription: String, subTotal: String, onDelete: () -> Unit) {
    ListItem(
        headlineContent = { Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(qtyDescription) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(subTotal, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        leadingContent = {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Icecream,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    )
}


@Composable
fun StickyCheckoutBottomBar(
    totalAmount: String,
    totalItems: Double, // O Int
    onConfirm: () -> Unit
) {
    Surface(
        shadowElevation = 16.dp, // Sombra fuerte para que flote
        tonalElevation = 3.dp,   // Color adaptativo MD3
        shape = MaterialTheme.shapes.large.copy(
            bottomStart = androidx.compose.foundation.shape.CornerSize(0.dp),
            bottomEnd = androidx.compose.foundation.shape.CornerSize(0.dp)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Información de Items y Total
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
//                horizontalAlignment = Alignment.CenterStart
            ) {
                // Cantidad de Items
                Text(
                    text = "Items: ${totalItems.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Precio
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Total:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.size(16.dp))
                    Text(
                        text = totalAmount, // Ejemplo: "$ 150.456"
                        style = MaterialTheme.typography.headlineMedium, // ¡Grande!
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface // Se adapta a tema claro/oscuro
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text(
                    text = "Confirmar Orden",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCheckoutScreen() {

    val mockkItems = listOf(
        CheckoutItemUi(1, "Caja palito bombón", "1.5", "$ 17.000", "$ 25.500"),
        CheckoutItemUi(2, "Helado 1kg", "1", "$ 10.000", "$ 10.000"),
        CheckoutItemUi(3, "Cucuruchos x10", "2", "$ 500", "$ 1.000")
    )

    val dummyState = Step3CheckoutOrderUiState.Success(
        clientName = "Kiosco Apu",
        clientAddress = "Olavarría 1020",
        totalItems = 3.0,
        totalAmount = "$ 36.500",
        itemsList = mockkItems
    )

    val snackbarHostState = SnackbarHostState()

    MaterialTheme {
        Step3ConfirmOrderScreen(
            state = dummyState,
            onBack = {},
            onConfirmOrder = {},
            onDeleteProduct = {},
            snackbarHostState = snackbarHostState
        )
    }
}