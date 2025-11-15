package com.example.storeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.storeapp.domain.model.Product
import com.example.storeapp.ui.navigation.AppScreens
import com.example.storeapp.ui.viewmodel.ProductListViewModel
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ExitToApp
// ===============================================================
//  COMPOSABLE REUTILIZABLE: PRODUCT ITEM (NO CAMBIA)
// ===============================================================
@Composable
fun ProductItem(
    product: Product,
    onAddToCart: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = product.title,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Fit
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$ ${product.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onAddToCart(product) }
                ) {
                    Text("Agregar al carrito")
                }
            }
        }
    }
}

// ===============================================================
//  PANTALLA CON TOPAPPBAR + BOTÓN DE CARRITO + SNACKBAR
// ===============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
     navController: NavController,
     onLogout: () -> Unit,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.snackbarMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Tienda de Productos") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(AppScreens.CartScreen.route)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Carrito de compras"
                        )
                    }

                    IconButton(onClick = {
                        viewModel.logout() // Llama a la lógica del ViewModel
                        onLogout()       // Ejecuta la acción de navegación
                    }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }


                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn {
                items(uiState.products) { product ->
                    ProductItem(
                        product = product,
                        onAddToCart = { selectedProduct ->
                            viewModel.addToCart(selectedProduct)
                        }
                    )
                }
            }
        }
    }
}
