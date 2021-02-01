package com.example.cryptochallenge.data.repository.dataSources

import com.example.cryptochallenge.data.local.dao.OrderBookDao
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.utils.OrderType
import com.example.cryptochallenge.utils.toOrderEntityList
import com.example.cryptochallenge.utils.toOrderList
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class OrderBookRoomDataSource(
    private val orderBookDao: OrderBookDao
) : LocalOrderBookDataSource {
    override fun getOrderBook(book: String): Single<OrderBook> {
        return Single.zip(
            orderBookDao.getOrderByBookAndType(book, OrderType.BIDS.type),
            orderBookDao.getOrderByBookAndType(book, OrderType.ASKS.type),
            { bids, asks ->
                OrderBook(asks.toOrderList(), bids.toOrderList())
            }
        )
            .subscribeOn(Schedulers.io())
    }

    override fun insertOrderBook(
        orderBook: OrderBook
    ): Single<Unit> {
        return orderBookDao.insertMany(
            orderBook.bids.toOrderEntityList(OrderType.BIDS),
            orderBook.asks.toOrderEntityList(OrderType.ASKS)
        ).subscribeOn(Schedulers.io())
    }

    override fun deleteOrderBookByBook(book: String): Single<Unit> {
        return orderBookDao.deleteOrdersByBook(book)
            .subscribeOn(Schedulers.io())
    }
}