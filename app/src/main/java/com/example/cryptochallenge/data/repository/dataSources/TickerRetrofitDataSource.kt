package com.example.cryptochallenge.data.repository.dataSources

import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.utils.toTicker
import javax.inject.Inject

class TickerRetrofitDataSource @Inject constructor(
    private val networkService: NetworkService
) : RemoteTickerDataSource {

    override suspend fun fetchTicker(book: String): Ticker {
        return networkService.doTickerCall(book).payload.toTicker()
    }
}