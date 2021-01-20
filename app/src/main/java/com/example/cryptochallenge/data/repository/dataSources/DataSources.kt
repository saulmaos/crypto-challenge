package com.example.cryptochallenge.data.repository.dataSources

import androidx.lifecycle.LiveData
import com.example.cryptochallenge.data.local.entity.BookEntity
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.repository.NetworkResponse
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface RemoteBooksDataSource {
    fun fetchAllBooks(): Single<List<Book>>
    fun getAllBooks(): LiveData<NetworkResponse<List<Book>>>
}

interface LocalBooksDataSource {
    fun getAllBooks(): Flowable<List<Book>>
    fun insertBooks(books: List<BookEntity>): Single<Unit>
}

interface RemoteTickerDataSource {
    fun fetchTicker(book: String)
    fun getTicker(): LiveData<NetworkResponse<Ticker>>
}

interface RemoteOrderDataSource {
    fun fetchOrderBooks(book: String)
    fun getLastOrderBook(): LiveData<NetworkResponse<OrderBook>>
}