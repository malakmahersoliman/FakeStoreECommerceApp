@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.fakestoreecommerceapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.fakestoreecommerceapp.viewmodel.CartViewModel
import com.example.fakestoreecommerceapp.models.Product
import com.example.fakestoreecommerceapp.util.Result
import com.example.fakestoreecommerceapp.viewmodel.ProductsViewModel

@Composable
fun ProductListScreen(
    viewModel: ProductsViewModel,
    cartViewModel: CartViewModel,
    onOpenCart: () -> Unit,
    onOpenCategories: () -> Unit,
    onOpenDetails: (Int) -> Unit,
    categoryId: Int? = null,
    onBack: (() -> Unit)? = null
) {
    val state by viewModel.products.collectAsState()

    LaunchedEffect(categoryId) {
        if (categoryId == null) viewModel.loadProducts()
        else viewModel.loadProductsByCategory(categoryId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (categoryId == null) "Products" else "Category Products") },
                navigationIcon = {
                    if (categoryId != null && onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    TextButton(onClick = onOpenCategories) { Text("Categories") }
                    IconButton(onClick = onOpenCart) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        }
    ) { padding ->
        when (val result = state) {
            is Result.Loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is Result.Success -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(result.data) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = { cartViewModel.add(product) },
                        onClick = { onOpenDetails(product.id) }
                    )
                }
            }

            is Result.Empty -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("No products found") }

            is Result.Error -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Error: ${result.message}", color = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            val imageUrl = product.images.firstOrNull().orEmpty()
            if (imageUrl.isNotBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
            }
            Text(product.title, style = MaterialTheme.typography.titleMedium)
            Text("Price: $${product.price}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onAddToCart) { Text("Add to Cart") }
        }
    }
}
