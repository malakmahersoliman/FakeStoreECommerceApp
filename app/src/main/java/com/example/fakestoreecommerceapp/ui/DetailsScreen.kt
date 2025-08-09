@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.fakestoreecommerceapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.fakestoreecommerceapp.util.Result
import com.example.fakestoreecommerceapp.viewmodel.CartViewModel
import com.example.fakestoreecommerceapp.viewmodel.DetailsViewModel
import kotlinx.coroutines.launch

@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    cartViewModel: CartViewModel,
    productId: Int,
    onBack: () -> Unit
) {
    // Avoid type inference issues by providing an initial value
    val state by viewModel.product.collectAsState(initial = Result.Loading)
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(productId) { viewModel.load(productId) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) },
        floatingActionButton = {
            if (state is Result.Success<*>) {
                FloatingActionButton(onClick = {
                    val p = (state as Result.Success<*>).data as com.example.fakestoreecommerceapp.models.Product
                    cartViewModel.add(p)
                    scope.launch { snackbarHost.showSnackbar("Added to cart") }
                }) { Text("+") }
            }
        }
    ) { padding ->
        when (val res = state) {
            is Result.Loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is Result.Error -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Error: ${res.message}", color = MaterialTheme.colorScheme.error) }

            is Result.Empty -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Not found") }

            is Result.Success<*> -> {
                val product = res.data as com.example.fakestoreecommerceapp.models.Product
                val imageUrl = product.images.firstOrNull().orEmpty()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = product.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(product.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Price: $${product.price}", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider()
                    Text(product.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(80.dp)) // space so FAB doesn't cover text
                }
            }
        }
    }
}
