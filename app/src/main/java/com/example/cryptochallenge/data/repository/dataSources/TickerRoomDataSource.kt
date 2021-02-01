package com.example.cryptochallenge.data.repository.dataSources

import com.example.cryptochallenge.data.local.dao.TickerDao
import com.example.cryptochallenge.data.local.entity.TickerEntity
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.utils.toTicker
import com.example.cryptochallenge.utils.toTickerEntity
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class TickerRoomDataSource(
    private val tickerDao: TickerDao
): LocalTickerDataSource {
    override fun getTicker(book: String): Single<Ticker> {
        return tickerDao.getTicker(book)
            .map(TickerEntity::toTicker)
            .subscribeOn(Schedulers.io())
    }

    override fun insertTicker(ticker: Ticker): Single<Unit> {
        return tickerDao.insert(ticker.toTickerEntity())
            .subscribeOn(Schedulers.io())
    }
}