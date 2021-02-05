package com.example.cryptochallenge.data.repository.dataSources

import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import io.reactivex.Maybe
import io.reactivex.Single

interface RemoteBooksDataSource {
    fun fetchAllBooks(): Single<List<Book>>
}

interface LocalBooksDataSource {
    fun getAllBooks(): Maybe<List<Book>>
    fun insertBooks(books: List<Book>): Single<Unit>
}

interface RemoteTickerDataSource {
    fun fetchTicker(book: String): Single<Ticker>
}

interface LocalTickerDataSource {
    fun getTicker(book: String): Single<Ticker>
    fun insertTicker(ticker: Ticker): Single<Unit>
}

interface RemoteOrderBookDataSource {
    fun fetchOrderBooks(book: String): Single<OrderBook>
}

interface LocalOrderBookDataSource {
    fun getOrderBook(book: String): Single<OrderBook>
    fun insertOrderBook(orderBook: OrderBook): Single<Unit>
    fun deleteOrderBookByBook(book: String): Single<Unit>
}