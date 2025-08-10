package com.example.fakestoreecommerceapp.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.fakestoreecommerceapp.models.Product
import com.example.fakestoreecommerceapp.network.RetrofitInstance

/**
 * Pages the /products endpoint using offset + limit.
 */
class ProductPagingSource(
    private val pageSize: Int = 10
) : PagingSource<Int, Product>() {

    private val api = RetrofitInstance.api

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val offset = params.key ?: 0
            val validExtensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp")
            val data = api.getProducts(offset, pageSize)
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

            val nextKey = if (data.isEmpty()) null else offset + pageSize
            val prevKey = if (offset == 0) null else (offset - pageSize).coerceAtLeast(0)

            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        // Try to get the key of the anchor position item, and derive the closest offset.
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(pageSize) ?: page.nextKey?.minus(pageSize)
    }
}
