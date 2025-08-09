package com.example.fakestoreecommerceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestoreecommerceapp.models.Product
import com.example.fakestoreecommerceapp.repository.StoreRepository
import com.example.fakestoreecommerceapp.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = StoreRepository(app.applicationContext)

    private val _product = MutableStateFlow<Result<Product>>(Result.Loading)
    val product: StateFlow<Result<Product>> = _product

    fun load(productId: Int) {
        viewModelScope.launch {
            _product.value = Result.Loading
            _product.value = repo.getProductDetails(productId)
        }
    }
}
