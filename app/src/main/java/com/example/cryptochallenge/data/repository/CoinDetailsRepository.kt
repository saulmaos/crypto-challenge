package com.example.cryptochallenge.data.repository

import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.repository.dataSources.LocalOrderBookDataSource
import com.example.cryptochallenge.data.repository.dataSources.LocalTickerDataSource
import com.example.cryptochallenge.data.repository.dataSources.RemoteOrderBookDataSource
import com.example.cryptochallenge.data.repository.dataSources.RemoteTickerDataSource
import io.reactivex.Single
import javax.inject.Inject

class CoinDetailsRepository @Inject constructor(
    private val remoteTickerDataSource: RemoteTickerDataSource,
    private val remoteOrderBookDataSource: RemoteOrderBookDataSource,
    private val localTickerDataSource: LocalTickerDataSource,
    private val localOrderBookDataSource: LocalOrderBookDataSource
) {
    suspend fun requestTicker(book: String): Ticker {
        return remoteTickerDataSource.fetchTicker(book)
    }

    suspend fun requestOrderBook(book: String): OrderBook {
        return remoteOrderBookDataSource.fetchOrderBooks(book)
    }

    fun getLocalTicker(book: String): Single<Ticker> {
        return localTickerDataSource.getTicker(book)
    }

    suspend fun saveTicker(ticker: Ticker) {
        localTickerDataSource.insertTicker(ticker)
    }

    suspend fun saveOrderBook(orderBook: OrderBook) {
        return localOrderBookDataSource.insertOrderBook(orderBook)
    }

    suspend fun deleteOrderBook(book: String) {
        return localOrderBookDataSource.deleteOrderBookByBook(book)
    }

    fun getLocalOrderBook(book: String): Single<OrderBook> {
        return localOrderBookDataSource.getOrderBook(book)
    }
}