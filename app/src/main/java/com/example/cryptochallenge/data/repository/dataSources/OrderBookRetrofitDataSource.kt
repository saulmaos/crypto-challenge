package com.example.cryptochallenge.data.repository.dataSources

import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.utils.toOrderBook
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class OrderBookRetrofitDataSource(
    private val networkService: NetworkService
) : RemoteOrderBookDataSource {
    override fun fetchOrderBooks(book: String): Single<OrderBook> {
        return networkService.doOrderBookCall(book)
            .map { it.payload.toOrderBook() }
            .subscribeOn(Schedulers.io())
    }
}