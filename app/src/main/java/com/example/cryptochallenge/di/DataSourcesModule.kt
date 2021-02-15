package com.example.cryptochallenge.di

import com.example.cryptochallenge.data.repository.dataSources.BooksRetrofitDataSource
import com.example.cryptochallenge.data.repository.dataSources.BooksRoomDataSource
import com.example.cryptochallenge.data.repository.dataSources.LocalBooksDataSource
import com.example.cryptochallenge.data.repository.dataSources.LocalOrderBookDataSource
import com.example.cryptochallenge.data.repository.dataSources.LocalTickerDataSource
import com.example.cryptochallenge.data.repository.dataSources.OrderBookRetrofitDataSource
import com.example.cryptochallenge.data.repository.dataSources.OrderBookRoomDataSource
import com.example.cryptochallenge.data.repository.dataSources.RemoteBooksDataSource
import com.example.cryptochallenge.data.repository.dataSources.RemoteOrderBookDataSource
import com.example.cryptochallenge.data.repository.dataSources.RemoteTickerDataSource
import com.example.cryptochallenge.data.repository.dataSources.TickerRetrofitDataSource
import com.example.cryptochallenge.data.repository.dataSources.TickerRoomDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
abstract class DataSourcesModule {

    @Binds
    abstract fun bindBookRetrofitDataSource(bookRetrofitDataSource: BooksRetrofitDataSource): RemoteBooksDataSource

    @Binds
    abstract fun bindBookRoomDataSource(booksRoomDataSource: BooksRoomDataSource): LocalBooksDataSource

    @Binds
    abstract fun bindOrderBookRetrofitDataSource(orderBookRetrofitDataSource: OrderBookRetrofitDataSource): RemoteOrderBookDataSource

    @Binds
    abstract fun bindOrderBookRoomDataSource(orderBookRoomDataSource: OrderBookRoomDataSource): LocalOrderBookDataSource

    @Binds
    abstract fun bindTickerRetrofitDataSource(tickerRetrofitDataSource: TickerRetrofitDataSource): RemoteTickerDataSource

    @Binds
    abstract fun bindTickerRoomDataSource(tickerRoomDataSource: TickerRoomDataSource): LocalTickerDataSource
}