package com.example.cryptochallenge.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.repository.dataSources.RemoteOrderDataSource
import com.example.cryptochallenge.data.repository.dataSources.RemoteTickerDataSource

class CoinDetailsRepository(
    private val remoteTickerDataSource: RemoteTickerDataSource,
    private val remoteOrderDataSource: RemoteOrderDataSource
) {
    val ticker: LiveData<NetworkResponse<Ticker>> = Transformations.map(
        remoteTickerDataSource.getTicker()
    ) {
        it
    }

    val orderBook: LiveData<NetworkResponse<OrderBook>> = Transformations.map(
        remoteOrderDataSource.getLastOrderBook()
    ) {
        it
    }

    fun requestTicker(book: String) {
        remoteTickerDataSource.fetchTicker(book)
    }

    fun requestLastOrder(book: String) {
        remoteOrderDataSource.fetchOrderBooks(book)
    }
}