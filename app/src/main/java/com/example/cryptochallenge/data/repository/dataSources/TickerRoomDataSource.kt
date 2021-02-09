package com.example.cryptochallenge.data.repository.dataSources

import com.example.cryptochallenge.data.local.dao.TickerDao
import com.example.cryptochallenge.data.local.entity.TickerEntity
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.utils.toTicker
import com.example.cryptochallenge.utils.toTickerEntity
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TickerRoomDataSource @Inject constructor(
    private val tickerDao: TickerDao
) : LocalTickerDataSource {
    override fun getTicker(book: String): Single<Ticker> {
        return tickerDao.getTicker(book)
            .map(TickerEntity::toTicker)
            .subscribeOn(Schedulers.io())
    }

    override suspend fun insertTicker(ticker: Ticker){
        tickerDao.insert(ticker.toTickerEntity())
    }
}