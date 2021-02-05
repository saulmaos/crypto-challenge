package com.example.cryptochallenge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cryptochallenge.data.local.entity.TickerEntity
import io.reactivex.Single

@Dao
interface TickerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ticker: TickerEntity): Single<Unit>

    @Query("SELECT * FROM ticker where book = :book")
    fun getTicker(book: String): Single<TickerEntity>
}