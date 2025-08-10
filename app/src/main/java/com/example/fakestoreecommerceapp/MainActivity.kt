package com.example.fakestoreecommerceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fakestoreecommerceapp.ui.*
import com.example.fakestoreecommerceapp.viewmodel.*
import com.example.fakestoreecommerceapp.ui.theme.FakeStoreECommerceAppTheme // ✅ Import your theme

class MainActivity : ComponentActivity() {
    // ViewModel for non-paged product listing (used in category screens)
    private val productsVM by viewModels<ProductsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            FakeStoreECommerceAppTheme {
                val nav = rememberNavController()

                NavHost(navController = nav, startDestination = "products") {

                    // Paged products (Task 5 start screen)
                    composable("products") {
                        val cartVM: CartViewModel = viewModel()
                        val pagedVM: ProductListPagingViewModel = viewModel()
                        ProductListPagingScreen(
                            viewModel = pagedVM,
                            cartViewModel = cartVM,
                            onOpenCart = { nav.navigate("cart") },
                            onOpenCategories = { nav.navigate("categories") },
                            onOpenDetails = { id -> nav.navigate("details/$id") }
                        )
                    }

                    // Categories
                    composable("categories") {
                        val catsVM: CategoriesViewModel = viewModel()
                        CategoriesScreen(
                            viewModel = catsVM,
                            onBack = { nav.popBackStack() },
                            onOpenCategory = { id -> nav.navigate("products_by_cat/$id") }
                        )
                    }

                    // Products by category (reuse non-paged)
                    composable("products_by_cat/{categoryId}") { entry ->
                        val categoryId = entry.arguments?.getString("categoryId")?.toIntOrNull()
                        val cartVM: CartViewModel = viewModel()
                        ProductListScreen(
                            viewModel = productsVM,
                            cartViewModel = cartVM,
                            categoryId = categoryId,
                            onOpenCart = { nav.navigate("cart") },
                            onOpenCategories = { nav.navigate("categories") },
                            onOpenDetails = { pid -> nav.navigate("details/$pid") },
                            onBack = { nav.popBackStack() }
                        )
                    }

                    // Product details
                    composable("details/{productId}") { entry ->
                        val productId = entry.arguments?.getString("productId")?.toIntOrNull()
                            ?: return@composable
                        val detailsVM: DetailsViewModel = viewModel()
                        val cartVM: CartViewModel = viewModel()
                        DetailsScreen(
                            viewModel = detailsVM,
                            cartViewModel = cartVM,
                            productId = productId,
                            onBack = { nav.popBackStack() }
                        )
                    }

                    // Cart
                    composable("cart") {
                        val cartVM: CartViewModel = viewModel()
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
