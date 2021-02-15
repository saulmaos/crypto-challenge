package com.example.cryptochallenge.data.repository.dataSources

import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.utils.toOrderBook
import javax.inject.Inject

class OrderBookRetrofitDataSource @Inject constructor(
    private val networkService: NetworkService
) : RemoteOrderBookDataSource {
    override suspend fun fetchOrderBooks(book: String): OrderBook {
        return networkService.doOrderBookCall(book).payload.toOrderBook()
    }
}