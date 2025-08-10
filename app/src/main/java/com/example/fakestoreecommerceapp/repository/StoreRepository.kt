package com.example.fakestoreecommerceapp.repository

import android.content.Context
import com.example.fakestoreecommerceapp.cart.AppDatabase
import com.example.fakestoreecommerceapp.cart.CartItem
import com.example.fakestoreecommerceapp.models.Category
import com.example.fakestoreecommerceapp.models.Product
import com.example.fakestoreecommerceapp.network.RetrofitInstance
import com.example.fakestoreecommerceapp.util.Result
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

class StoreRepository(appContext: Context) {

    private val api = RetrofitInstance.api
    private val cartDao = AppDatabase.get(appContext).cartDao()

    // ==================== NETWORK METHODS ====================
    suspend fun getProducts(offset: Int, limit: Int): Result<List<Product>> {
        return try {
            val validExtensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp")
            val data = api.getProducts(offset, limit)
                .map { product ->
                    product.copy(
                        images = product.images.filter { url ->
                            validExtensions.any { ext -> url.endsWith(ext) }
                        }
                    )
                }
                .filter { product ->
                    product.title.isNotBlank() && product.price >= 0 && product.images.isNotEmpty()
                }
                .distinctBy { it.id }
            if (data.isEmpty()) Result.Empty else Result.Success(data)
        } catch (e: HttpException) {
            Result.Error("HTTP ${e.code()} ${e.message()}", e, e.code())
        } catch (e: IOException) {
            Result.Error("Network error: ${e.message}", e)
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}", e)
        }
    }

    suspend fun getProductDetails(id: Int): Result<Product> {
        return try {
            val p = api.getProductDetails(id)
            Result.Success(p)
        } catch (e: HttpException) {
            Result.Error("HTTP ${e.code()} ${e.message()}", e, e.code())
        } catch (e: IOException) {
            Result.Error("Network error: ${e.message}", e)
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}", e)
        }
    }

    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val validExtensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp")
            val data = api.getCategories()
                .filter { category ->
                    category.name.isNotBlank() && category.image.let { url ->
                        validExtensions.any { ext -> url.endsWith(ext, ignoreCase = true) }
                    }
                }
                .distinctBy { it.id }

            if (data.isEmpty()) Result.Empty else Result.Success(data)

        } catch (e: HttpException) {
            Result.Error("HTTP ${e.code()} ${e.message()}", e, e.code())
        } catch (e: IOException) {
            Result.Error("Network error: ${e.message}", e)
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}", e)
        }
    }

    suspend fun getProductsByCategory(
        categoryId: Int,
        offset: Int? = null,
        limit: Int? = null
    ): Result<List<Product>> {
        return try {
            val data = api.getProductsByCategory(categoryId, offset, limit)
                .filter { it.title.isNotBlank() && it.price >= 0 }
                .distinctBy { it.id }
            if (data.isEmpty()) Result.Empty else Result.Success(data)
        } catch (e: HttpException) {
            Result.Error("HTTP ${e.code()} ${e.message()}", e, e.code())
        } catch (e: IOException) {
            Result.Error("Network error: ${e.message}", e)
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}", e)
        }
    }

    // ==================== LOCAL CART METHODS ====================
    fun observeCart(): Flow<List<CartItem>> = cartDao.getAll()

    suspend fun addToCart(product: Product) {
        val image = product.images.firstOrNull() ?: ""
        val cartItem = CartItem(
            productId = product.id,
            title = product.title,
            price = product.price,
            imageUrl = image,
            quantity = 1
        )
        cartDao.addOrIncrement(cartItem)
    }

    suspend fun incrementCart(productId: Int) {
        cartDao.increment(productId, 1)
    }

    suspend fun decrementCart(productId: Int) {
        cartDao.decrementOrDelete(productId, 1)
    }

    suspend fun removeFromCart(productId: Int) = cartDao.delete(productId)
    suspend fun clearCart() = cartDao.clear()
}
