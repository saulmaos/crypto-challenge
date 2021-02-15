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
    suspend fun fetchTicker(book: String): Ticker
}

interface LocalTickerDataSource {
    fun getTicker(book: String): Single<Ticker>
    suspend fun insertTicker(ticker: Ticker)
}

interface RemoteOrderBookDataSource {
    suspend fun fetchOrderBooks(book: String): OrderBook
}

interface LocalOrderBookDataSource {
    fun getOrderBook(book: String): Single<OrderBook>
    suspend fun insertOrderBook(orderBook: OrderBook)
    suspend fun deleteOrderBookByBook(book: String)
}