package com.example.storeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.storeapp.data.local.CartItem
import com.example.storeapp.ui.viewmodel.CartViewModel


// ================================================================
// COMPOSABLE REUTILIZABLE PARA UN ITEM DEL CARRITO
// ================================================================
@Composable
fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.image,
                contentDescription = item.title,
                modifier = Modifier.size(80.dp).padding(end = 16.dp),
                contentScale = ContentScale.Fit
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("$ ${item.price}")
            }
            // --- CÓDIGO CORREGIDO Y VERIFICADO ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onQuantityChange(item.quantity - 1) }) {
                    Icon(imageVector = Icons.Filled.Remove, contentDescription = "Quitar uno")
                }
                Text(item.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Añadir uno")
                }
                IconButton(onClick = onRemove) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// ================================================================
// PANTALLA PRINCIPAL DEL CARRITO
// ================================================================
@Composable
fun CartScreen(
    onCheckout: () -> Unit, // Función para navegar atrás después de comprar
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.cartItems.isEmpty()) {
        // Muestra un mensaje si el carrito está vacío
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tu carrito está vacío")
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            // Lista de items del carrito
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.cartItems, key = { it.id }) { item ->
                    CartItemRow(
                        item = item,
                        onQuantityChange = { newQuantity ->
                            viewModel.onQuantityChange(item.id, newQuantity)
                        },
                        onRemove = {
                            viewModel.removeItemFromCart(item.id)
                        }
                    )
                }
            }
            // Resumen y botón de pagar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // --- CORRECCIÓN APLICADA AQUÍ ---
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total:", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "$ ${"%.2f".format(uiState.totalPrice)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.checkout()
                        onCheckout()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pagar")
                }
                // Botón para vaciar el carrito
                OutlinedButton(
                    onClick = { viewModel.clearCart() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Vaciar carrito")
                }

            }
        }
    }
}