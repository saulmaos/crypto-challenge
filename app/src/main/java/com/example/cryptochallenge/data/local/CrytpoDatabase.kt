package com.example.cryptochallenge.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cryptochallenge.data.local.dao.BookDao
import com.example.cryptochallenge.data.local.entity.BookEntity

@Database(entities = [BookEntity::class], version = 1, exportSchema = true)
abstract class CryptoDatabase: RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: CryptoDatabase? = null

        fun getDatabase(context: Context): CryptoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CryptoDatabase::class.java,
                    "crypto_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}