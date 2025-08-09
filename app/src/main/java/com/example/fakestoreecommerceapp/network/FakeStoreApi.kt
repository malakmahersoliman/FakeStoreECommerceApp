package com.example.fakestoreecommerceapp.network

import com.example.fakestoreecommerceapp.models.Category
import com.example.fakestoreecommerceapp.models.Product
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FakeStoreApi {

    @GET("products")
    suspend fun getProducts(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): List<Product>

    @GET("products/{id}")
    suspend fun getProductDetails(@Path("id") id: Int): Product

    @GET("categories")
    suspend fun getCategories(): List<Category>

    @GET("categories/{id}/products")
    suspend fun getProductsByCategory(
        @Path("id") categoryId: Int,
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null
    ): List<Product>
}
