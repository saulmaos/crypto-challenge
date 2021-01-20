package com.example.cryptochallenge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cryptochallenge.data.local.entity.BookEntity
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMany(books: List<BookEntity>): Single<Unit>

    @Query("SELECT * FROM books")
    fun getAllBooks(): Flowable<List<BookEntity>>
}