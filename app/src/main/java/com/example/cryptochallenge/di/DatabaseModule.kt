package com.example.cryptochallenge.di

import android.content.Context
import com.example.cryptochallenge.data.local.CryptoDatabase
import com.example.cryptochallenge.data.local.dao.BookDao
import com.example.cryptochallenge.data.local.dao.OrderBookDao
import com.example.cryptochallenge.data.local.dao.TickerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): CryptoDatabase =
        CryptoDatabase.getDatabase(context)

    @Provides
    fun provideBookDao(database: CryptoDatabase): BookDao =
        database.bookDao()

    @Provides
    fun provideOrderBookDao(database: CryptoDatabase): OrderBookDao =
        database.orderBookDao()

    @Provides
    fun provideTickerDao(database: CryptoDatabase): TickerDao =
        database.tickerDao()
}