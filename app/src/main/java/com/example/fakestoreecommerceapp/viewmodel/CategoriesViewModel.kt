package com.example.fakestoreecommerceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestoreecommerceapp.models.Category
import com.example.fakestoreecommerceapp.repository.StoreRepository
import com.example.fakestoreecommerceapp.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = StoreRepository(app.applicationContext)

    private val _categories = MutableStateFlow<Result<List<Category>>>(Result.Loading)
    val categories: StateFlow<Result<List<Category>>> = _categories

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = Result.Loading
            val result = repo.getCategories()
            if (result is Result.Success) {
                val filtered = result.data.filter {
                    it.name.isNotBlank() && !it.name.contains("string", ignoreCase = true)
                }
                _categories.value = Result.Success(filtered)
            } else {
                _categories.value = result
            }
        }
    }

}