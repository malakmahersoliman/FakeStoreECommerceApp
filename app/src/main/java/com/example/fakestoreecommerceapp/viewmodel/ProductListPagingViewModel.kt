package com.example.fakestoreecommerceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.fakestoreecommerceapp.models.Product
import com.example.fakestoreecommerceapp.repository.ProductPagingSource
import kotlinx.coroutines.flow.Flow

class ProductListPagingViewModel(app: Application) : AndroidViewModel(app) {


    val products: Flow<PagingData<Product>> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ProductPagingSource(pageSize = 10) }
        ).flow
}
