package com.barradev.chester.features.seller.presentation.home


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.barradev.chester.features.seller.presentation.home.model.OrderUiModel

private val VioletaChester = Color(0xFF5E4B8B)

@Composable
fun HomeSellerRoute(
    viewmodel: HomeSellerViewModel = hiltViewModel()
) {
    val state by viewmodel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewmodel.syncOrders()
    }

    HomeSellerScreen(
        state,
        viewmodel::onNavToOrderDetail,
        viewmodel::onNavToCreateOrder,
        viewmodel::syncOrders
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSellerScreen(
    state: HomeSellerStateUi,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCreateOrder: () -> Unit,
    refreshData: () -> Unit,
) {

    Box(Modifier.fillMaxSize()) {

        Column(Modifier.fillMaxSize()) {

            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Ordenes",
                        style = MaterialTheme.typography.titleLarge
                    )
                })

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when (val currentState = state) {
                    is HomeSellerStateUi.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = VioletaChester
                        )
                    }

                    is HomeSellerStateUi.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Ocurrió un error",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                            Text(currentState.message, color = Color.Gray)
                        }
                    }

                    is HomeSellerStateUi.Success -> {
                        PullToRefreshBox(
                            isRefreshing = false,
                            onRefresh = { refreshData() }
                        ) {
                            if (currentState.orders.isEmpty()) {
                                Text(
                                    text = "No hay ventas registradas",
                                    modifier = Modifier.align(Alignment.Center),
                                    color = Color.Gray
                                )
                            } else {

                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(
                                        items = currentState.orders,
                                        key = { it.id }
                                    ) { orderUi ->
                                        OrderItemCard(
                                            order = orderUi,
                                            // Navigate to orderDetail
                                            onClick = { onNavigateToDetail(orderUi.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        LargeFloatingActionButton(
            onClick = onNavigateToCreateOrder,
            content = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear nueva orden",
                )
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 16.dp, vertical = 30.dp)
        )
    }

}


@Composable
fun OrderItemCard(
    order: OrderUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = VioletaChester
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Column {

                    // Title
                    Text(
                        text = order.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = Color.White
                    )

                    // Date
                    Text(
                        text = order.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                // Navigate to OrderDetail
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            // File 1: Total amount
            InfoRow(label = "Monto Total:", value = order.totalAmount)
            Spacer(modifier = Modifier.height(4.dp))

            // File 2: Count items
            InfoRow(label = "Cantidad items:", value = order.itemsCount.toString()) // "$125000"
            Spacer(modifier = Modifier.height(4.dp))

            // File 3: State
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                StatusChip(label = order.statusLabel, color = order.statusColor)
            }

            // Spacer(modifier = Modifier.height(4.dp))

            // Fila 4: Forma de Pago
            // InfoRow(label = "Forma pago", value = order.paymentMethod)
        }
    }
}


// --- Helpers ---

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = Color.White
        )
    }
}

@Composable
private fun StatusChip(
    label: String,
    color: Color
) {

    Box(
        modifier = Modifier
            .background(color = color, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeSellerScreenPreview() {

    val mockOrders = listOf(
        OrderUiModel(
            id = 1,
            title = "Lo de pepito",
            date = "13/10/2023",
            totalAmount = "$125000",
            statusLabel = "Pendiente",
            statusColor = Color(0xFFFF9800),
            paymentMethod = "Efectivo",
            itemsCount = 12.0
        ),
        OrderUiModel(
            id = 2,
            title = "Kiosco El 24",
            date = "12/10/2023",
            totalAmount = "$45200",
            statusLabel = "Sincronizado",
            statusColor = Color(0xFF4CAF50),
            paymentMethod = "Transferencia",
            itemsCount = 7.0
        )
    )

    val mockHomeStateUI = HomeSellerStateUi.Success(
        orders = mockOrders
    )

    HomeSellerScreen(
        state = mockHomeStateUI,
        onNavigateToDetail = {},
        onNavigateToCreateOrder = {}
    ) { }
}