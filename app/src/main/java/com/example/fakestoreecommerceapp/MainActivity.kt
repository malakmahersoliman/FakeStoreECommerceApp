package com.example.fakestoreecommerceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fakestoreecommerceapp.cart.CartViewModel
import com.example.fakestoreecommerceapp.ui.CartScreen
import com.example.fakestoreecommerceapp.ui.ProductListScreen
import com.example.fakestoreecommerceapp.ui.ProductsViewModel

class MainActivity : ComponentActivity() {

    // AndroidViewModel subclasses work with default factory
    private val productsVM by viewModels<ProductsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val nav = rememberNavController()

                NavHost(navController = nav, startDestination = "products") {
                    composable("products") {
                        // Create CartViewModel in composable scope
                        val cartVM: CartViewModel = viewModel()
                        ProductListScreen(
                            viewModel = productsVM,
                            cartViewModel = cartVM,
                            onOpenCart = { nav.navigate("cart") }
                        )
                    }
                    composable("cart") {
                        val cartVM: com.example.fakestoreecommerceapp.cart.CartViewModel =
                            androidx.lifecycle.viewmodel.compose.viewModel()
                        CartScreen(
                            viewModel = cartVM,
                            onBack = { nav.popBackStack() }
                        )
                    }

                }
            }
        }
    }
}

