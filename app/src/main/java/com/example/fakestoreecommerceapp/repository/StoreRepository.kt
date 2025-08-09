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

class StoreRepository(private val appContext: Context) {

    private val api = RetrofitInstance.api
    private val cartDao = AppDatabase.get(appContext).cartDao()

    // ==================== NETWORK METHODS (Task 1) ====================
    suspend fun getProducts(offset: Int, limit: Int): Result<List<Product>> {
        return try {
            val data = api.getProducts(offset, limit)
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
            val data = api.getProductDetails(id)
            Result.Success(data)
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
            val data = api.getCategories()
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
            if (data.isEmpty()) Result.Empty else Result.Success(data)
        } catch (e: HttpException) {
            Result.Error("HTTP ${e.code()} ${e.message()}", e, e.code())
        } catch (e: IOException) {
            Result.Error("Network error: ${e.message}", e)
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}", e)
        }
    }

    // ==================== LOCAL CART METHODS (Task 2) ====================
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
        cartDao.addOrIncrement(cartItem) // <-- use cartItem here
    }


    suspend fun incrementCart(productId: Int) {

        val affected = cartDao.increment(productId, 1)
        if (affected == 0) {
        }
    }

    suspend fun decrementCart(productId: Int) {
        cartDao.decrementOrDelete(productId, 1)
    }

    suspend fun removeFromCart(productId: Int) = cartDao.delete(productId)
    suspend fun clearCart() = cartDao.clear()
}
