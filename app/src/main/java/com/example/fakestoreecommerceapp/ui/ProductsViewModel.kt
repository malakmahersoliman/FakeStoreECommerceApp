package com.example.fakestoreecommerceapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestoreecommerceapp.models.Product
import com.example.fakestoreecommerceapp.repository.StoreRepository
import com.example.fakestoreecommerceapp.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = StoreRepository(application.applicationContext)

    private val _products = MutableStateFlow<Result<List<Product>>>(Result.Loading)
    val products = _products.asStateFlow()

    fun loadProducts(offset: Int = 0, limit: Int = 10) {
        viewModelScope.launch {
            _products.value = Result.Loading
            _products.value = repo.getProducts(offset, limit)
        }
    }
}