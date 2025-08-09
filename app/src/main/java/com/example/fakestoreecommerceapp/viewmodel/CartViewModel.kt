package com.example.fakestoreecommerceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestoreecommerceapp.models.Product
import com.example.fakestoreecommerceapp.repository.StoreRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = StoreRepository(app.applicationContext)

    val cartItems = repo.observeCart()
        .stateIn(viewModelScope, SharingStarted.Companion.Lazily, emptyList())

    val totalPrice = cartItems
        .map { list -> list.sumOf { it.price * it.quantity } }
        .stateIn(viewModelScope, SharingStarted.Companion.Lazily, 0.0)

    fun add(product: Product) = viewModelScope.launch { repo.addToCart(product) }
    fun increment(productId: Int) = viewModelScope.launch { repo.incrementCart(productId) }
    fun decrement(productId: Int) = viewModelScope.launch { repo.decrementCart(productId) }
    fun remove(productId: Int) = viewModelScope.launch { repo.removeFromCart(productId) }
    fun clear() = viewModelScope.launch { repo.clearCart() }
}