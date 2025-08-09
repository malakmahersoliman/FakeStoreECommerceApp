@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.fakestoreecommerceapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.fakestoreecommerceapp.models.Product
import com.example.fakestoreecommerceapp.viewmodel.CartViewModel
import com.example.fakestoreecommerceapp.viewmodel.ProductListPagingViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductListPagingScreen(
    viewModel: ProductListPagingViewModel,
    cartViewModel: CartViewModel,
    onOpenCart: () -> Unit,
    onOpenCategories: () -> Unit,
    onOpenDetails: (Int) -> Unit
) {
    val products: LazyPagingItems<Product> = viewModel.products.collectAsLazyPagingItems()
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products") },
                actions = {
                    TextButton(onClick = onOpenCategories) { Text("Categories") }
                    IconButton(onClick = onOpenCart) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        }
    ) { padding ->

        // Initial loading
        if (products.loadState.refresh is LoadState.Loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        // Initial error
        (products.loadState.refresh as? LoadState.Error)?.let {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Failed to load products.", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { products.retry() }) { Text("Retry") }
                }
            }
            return@Scaffold
        }

        // Empty after refresh
        if (products.itemCount == 0) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No products found")
            }
            return@Scaffold
        }

        // List (index-based access – no paging items() extension required)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(count = products.itemCount, key = { index ->
                products.peek(index)?.id ?: index // stable key if available
            }) { index ->
                val product = products[index]
                if (product != null) {
                    ProductCardPaged(
                        product = product,
                        onAddToCart = {
                            cartViewModel.add(product)
                            scope.launch { snackbarHost.showSnackbar("Added to cart") }
                        },
                        onClick = { onOpenDetails(product.id) }
                    )
                } else {
                    PlaceholderCard()
                }
            }

            when (products.loadState.append) {
                is LoadState.Loading -> item { AppendLoadingRow() }
                is LoadState.Error -> item { AppendErrorRow(onRetry = { products.retry() }) }
                else -> {}
            }
        }
    }
}

@Composable
private fun ProductCardPaged(
    product: Product,
    onAddToCart: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
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

@Composable private fun PlaceholderCard() {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Box(Modifier.height(160.dp).padding(16.dp))
    }
}

@Composable private fun AppendLoadingRow() {
    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
        CircularProgressIndicator()
    }
}

@Composable private fun AppendErrorRow(onRetry: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
        Button(onClick = onRetry) { Text("Retry") }
    }
}
