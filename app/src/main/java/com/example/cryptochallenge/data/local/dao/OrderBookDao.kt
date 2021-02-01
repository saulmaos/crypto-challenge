package com.example.cryptochallenge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cryptochallenge.data.local.entity.OrderEntity
import io.reactivex.Single

@Dao
interface OrderBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMany(orderBids: List<OrderEntity>, orderAsks: List<OrderEntity>): Single<Unit>

    @Query("DELETE FROM `order` WHERE book = :book")
    fun deleteOrdersByBook(book: String): Single<Unit>

    @Query("SELECT * FROM `order` WHERE book = :book AND order_type = :orderType")
    fun getOrderByBookAndType(book: String, orderType: String): Single<List<OrderEntity>>
}