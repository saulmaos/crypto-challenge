package com.example.cryptochallenge.data.repository

import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.repository.dataSources.LocalOrderBookDataSource
import com.example.cryptochallenge.data.repository.dataSources.LocalTickerDataSource
import com.example.cryptochallenge.data.repository.dataSources.RemoteOrderBookDataSource
import com.example.cryptochallenge.data.repository.dataSources.RemoteTickerDataSource
import io.reactivex.Single

class CoinDetailsRepository(
    private val remoteTickerDataSource: RemoteTickerDataSource,
    private val remoteOrderBookDataSource: RemoteOrderBookDataSource,
    private val localTickerDataSource: LocalTickerDataSource,
    private val localOrderBookDataSource: LocalOrderBookDataSource
) {
    fun requestTicker(book: String): Single<Ticker> {
        return remoteTickerDataSource.fetchTicker(book)
    }

    fun requestOrderBook(book: String): Single<OrderBook> {
        return remoteOrderBookDataSource.fetchOrderBooks(book)
    }

    fun getLocalTicker(book: String): Single<Ticker> {
        return localTickerDataSource.getTicker(book)
    }

    fun saveTicker(ticker: Ticker): Single<Unit> {
        return localTickerDataSource.insertTicker(ticker)
    }

    fun saveOrderBook(orderBook: OrderBook): Single<Unit> {
        return localOrderBookDataSource.insertOrderBook(orderBook)
    }

    fun deleteOrderBook(book: String): Single<Unit> {
        return localOrderBookDataSource.deleteOrderBookByBook(book)
    }

    fun getLocalOrderBook(book: String): Single<OrderBook> {
        return localOrderBookDataSource.getOrderBook(book)
    }
}