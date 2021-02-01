package com.example.cryptochallenge.data.repository.dataSources

import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.utils.toTicker
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class TickerRetrofitDataSource(
    private val networkService: NetworkService
) : RemoteTickerDataSource {

    override fun fetchTicker(book: String): Single<Ticker> {
        return networkService.doTickerCall(book)
            .map { it.payload.toTicker() }
            .subscribeOn(Schedulers.io())
    }
}