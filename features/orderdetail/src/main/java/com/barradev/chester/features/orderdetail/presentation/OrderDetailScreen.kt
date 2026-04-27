package com.barradev.chester.features.orderdetail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.barradev.chester.core.model.models.OrderDetail
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OrderDetailRoute(
    viewModel: OrderDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    OrderDetailScreen(
        state = state,
        onRetry = viewModel::getDetails,
        onBackClick = viewModel::navToBack
    )
}

// --- SCREEN PRINCIPAL ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    state: OrderDetailUiState,
    onRetry: () -> Unit,
    onBackClick: () -> Unit
) {

    // 1. Lógica del Título Dinámico
    val topBarTitle = when (state) {
        is OrderDetailUiState.Success -> "Detalle del Pedido #${state.topBarTitle}"
        else -> "Detalle del Pedido"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, topBar = {
            CenterAlignedTopAppBar(
                title = { Text(topBarTitle) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }) { innerPadding ->

        // Contenedor para todos los estados
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            // Switch de Estados
            when (state) {
                is OrderDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                is OrderDetailUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onRetry) { Text("Reintentar") }
                    }
                }

                is OrderDetailUiState.Success -> {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Card Cabecera con datos
                        item {
                            OrderHeaderCard(
                                name = state.headerName,
                                address = state.headerAddress,
                                date = state.headerDate,
                                itemsCount = state.headerItemsCount,
                                total = state.headerTotalAmount
                            )
                        }

                        //  Título
                        item {
                            Text(
                                text = "Productos",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                            )
                        }

                        // Lista de productos
                        items(state.products, key = { it.id }) { product ->
                            ProductItemRow(product)
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }

                        item { Spacer(modifier = Modifier.height(30.dp)) }
                    }
                }
            }
        }
    }
}


// --- Componentes especificos ---

@Composable
fun OrderHeaderCard(
    name: String,
    address: String,
    date: String,
    itemsCount: String,
    total: String
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    InfoRow(icon = Icons.Default.LocationOn, text = address)
                    InfoRow(icon = Icons.Default.DateRange, text = date)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = itemsCount,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total a Pagar",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = total,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ProductItemRow(product: ProductDetailUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cantidad
        Surface(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(45.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "${product.quantity}x",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Nombre y Precio Unitario
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "PU: $${product.unitPrice}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Subtotal
        Text(
            text = product.subTotal,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


// --- PREVIEWS ---

@Preview(showBackground = true)
@Composable
fun PreviewProductItemRow() {
    MaterialTheme {
        ProductItemRow(
            product = ProductDetailUi(
                id = 1L,
                quantity = "2x",
                name = "Producto de Prueba",
                unitPrice = "PU: $ 1.500,00",
                subTotal = "$ 3.000,00"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOrderHeader() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            OrderHeaderCard(
                name = "Juan Pérez",
                address = "Calle Falsa 123, Buenos Aires",
                date = "03 de Abril, 2026",
                itemsCount = "5 ítems",
                total = "$ 15.450,00"
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewOrderDetailScreenSuccess() {
    MaterialTheme {
        val mockState = OrderDetailUiState.Success(
            topBarTitle = "1004",
            headerName = "Juan Pérez",
            headerAddress = "Calle Falsa 123",
            headerDate = "03 de Abril, 2026",
            headerItemsCount = "2 ítems",
            headerTotalAmount = "$ 4.500,00",
            products = listOf(
                ProductDetailUi(
                    id = 1L,
                    quantity = "1x",
                    name = "Teclado Mecánico",
                    unitPrice = "1.500,00",
                    subTotal = "$ 1.500,00"
                ),
                ProductDetailUi(
                    id = 2L,
                    quantity = "2x",
                    name = "Mouse Inalámbrico",
                    unitPrice = "1.500,00",
                    subTotal = "$ 3.000,00"
                )
            )
        )

        OrderDetailScreen(
            state = mockState,
            onRetry = {},
            onBackClick = {}
        )
    }
}