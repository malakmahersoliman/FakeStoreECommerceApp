package com.example.fakestoreecommerceapp.cart

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items")
    fun getAll(): Flow<List<CartItem>>

    @Query("DELETE FROM cart_items WHERE productId = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clear()

    // insert-or-ignore (for first add)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: CartItem): Long

    // --- quantity updates ---
    @Query("UPDATE cart_items SET quantity = quantity + :by WHERE productId = :id")
    suspend fun increment(id: Int, by: Int = 1): Int

    @Query("UPDATE cart_items SET quantity = quantity - :by WHERE productId = :id")
    suspend fun decrementRaw(id: Int, by: Int = 1): Int

    @Query("SELECT quantity FROM cart_items WHERE productId = :id")
    suspend fun getQuantity(id: Int): Int?

    @Transaction
    suspend fun addOrIncrement(item: CartItem) {
        val updated = increment(item.productId, 1)
        if (updated == 0) insert(item)
    }

    @Transaction
    suspend fun decrementOrDelete(id: Int, by: Int = 1) {
        val q = getQuantity(id) ?: return
        if (q > by) {
            decrementRaw(id, by)
        } else {

            delete(id)
        }
    }
}
