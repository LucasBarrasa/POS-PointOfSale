package com.barradev.chester.features.createorder.presentation.steps.step2SelectProducts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// --- ROUTE ---
@Composable
fun Step2SelectProductsRoute(
    viewModel: Step2SelectProductsViewModel = hiltViewModel()
) {
    val stateUi by viewModel.uiState.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val listScrollState = rememberLazyListState()


    LaunchedEffect(stateUi.searchQuery) {
        if (listScrollState.firstVisibleItemIndex > 0) {
            listScrollState.scrollToItem(0)
        }
    }

    Step2SelectProductsScreen(
        state = stateUi,
        searchQuery = searchQuery,
        onNavigateToConfirmOrder = viewModel::onNavigateToConfirmOrder,
        onSearchQueryChange = viewModel::onQuerySearchChange,
        onAddProduct = viewModel::onAddProduct,
        onSumQuantity = viewModel::onAddOneQuantity,
        onRestQuantity = viewModel::onRestQuantity,
        onDeleteProduct = viewModel::onDeleteLocalOrder,
        onNavigateBack = viewModel::onNavigateBack,
        onNavigateToHome = viewModel::onNavigateToHome,
        listState = listScrollState
    )
}

// --- SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2SelectProductsScreen(
    state: Step2SelectProductsUiState,
    onNavigateToConfirmOrder: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onAddProduct: (Long) -> Unit,
    onSumQuantity: (Long?) -> Unit,
    onRestQuantity: (Long?) -> Unit,
    onDeleteProduct: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    listState: LazyListState,
    searchQuery: String
) {
    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Seleccion de Productos",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = "Navegar a Inicio",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212),
                    scrolledContainerColor = Color(0xFF121212),
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
                .padding(end = 8.dp)
                .fillMaxSize()
        ) {

            // BARRA DE BÚSQUEDA
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Buscar Producto ...", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CleaningServices,
                                contentDescription = "Limpiar texto",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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

            // LISTA DE PRODUCTOS
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = state.productsList,
                    key = { it.id }
                ) { product ->

                    ProductCard(
                        product = product,
                        onAddItem = { onAddProduct(product.id) },
                        onRemoveItem = {
                            product.orderDetailId?.let { onDeleteProduct(it) }
                        },
                        onSumQuantity = {
                            onSumQuantity(product.orderDetailId)
                        },
                        onRestQuantity = { onRestQuantity(product.orderDetailId) },
                        onCommentClick = { }
                    )
                }
            }

            // BOTÓN CONFIRMAR
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Color.DarkGray)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    color = Color.White,
                    text = "Items: ${state.totalItemsCount}"
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    color = Color.White,
                    text = "Total: $${state.totalAmount.toInt()}"
                )

                Spacer(modifier = Modifier.size(8.dp))

                TextButton(
                    onClick = onNavigateToConfirmOrder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color.Green, shape = RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Confirmar Orden",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- PRODUCT CARD ---
@Composable
fun ProductCard(
    product: ProductItemUiState,
    onAddItem: () -> Unit,
    onRestQuantity: () -> Unit,
    onSumQuantity: () -> Unit,
    onRemoveItem: () -> Unit,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(12.dp)

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .then(
                if (product.isAdded) Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    cardShape
                ) else Modifier
            ),
        shape = cardShape,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {


        Column(modifier = Modifier.padding(12.dp)) {
            // --- FILA SUPERIOR (Info y Botón Añadir) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Stock: ${product.currentStock} | $ ${product.price}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Botón principal: Cambia color y texto según el estado
                Button(
                    onClick = { if (!product.isAdded) onAddItem() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (product.isAdded) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primary,
                        contentColor = if (product.isAdded) Color(0xFF2E7D32) else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (product.isAdded) "En Orden" else "Añadir")
                }
            }

            // --- SECCIÓN EXPANDIBLE (Solo si está añadido) ---
            AnimatedVisibility(
                visible = product.isAdded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray
                    )

                    // BARRA DE HERRAMIENTAS COMPLETA
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // IZQUIERDA: Control de cantidad
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.background(
                                Color(0xFFF5F5F5),
                                RoundedCornerShape(8.dp)
                            )
                        ) {
                            IconButton(
                                onClick = onRestQuantity,
                                enabled = product.quantityInOrder > 1
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Menos",
                                    tint = Color.Black
                                )
                            }

                            // Campo de cantidad (Manual)
                            Box(
                                modifier = Modifier
                                    .width(45.dp)
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = product.quantityInOrder.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            IconButton(onClick = onSumQuantity) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Más",
                                    tint = Color.Black
                                )
                            }
                        }

                        // MEDIO: Botón Rápido +0.5
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.height(40.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Surtido", style = MaterialTheme.typography.labelLarge)
                        }

                        // DERECHA: Comentario y Eliminar
                        Row {
                            IconButton(onClick = onCommentClick) {
                                Icon(
                                    Icons.AutoMirrored.Outlined.Comment,
                                    contentDescription = "Nota",
                                    tint = Color.Gray
                                )
                            }
                            IconButton(onClick = onRemoveItem) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color(0xFFD32F2F)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewStep2SelectProductsScreen() {

    val listProductMock = listOf(
        ProductItemUiState(
            id = 1,
            orderDetailId = 2,
            name = "Helado",
            price = 15000.0,
            currentStock = 10.0,
            quantityInOrder = 3.5
        ),
        ProductItemUiState(
            id = 2,
            orderDetailId = 1,
            name = "Helado 2",
            price = 13500.0,
            currentStock = 50.0,
            quantityInOrder = 0.0,
        ),
    )

    val lazyListState = rememberLazyListState()

    val mockStep2SelectProductsUiState = Step2SelectProductsUiState(
        productsList = listProductMock,
        searchQuery = "",
        totalItemsCount = 13.0,
        totalAmount = 153400.0
    )

    Step2SelectProductsScreen(
        state = mockStep2SelectProductsUiState,
        searchQuery = "",
        onNavigateToConfirmOrder = {},
        onSearchQueryChange = {},
        onAddProduct = {},
        onSumQuantity = {},
        onRestQuantity = {},
        onDeleteProduct = {},
        onNavigateBack = {},
        onNavigateToHome = {},
        listState = lazyListState,
    )

}