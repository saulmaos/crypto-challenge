package com.example.cryptochallenge.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.local.entity.BookEntity
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.model.Order
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.data.remote.response.BaseResponse
import com.example.cryptochallenge.data.remote.response.PayloadOrderBookResponse
import com.example.cryptochallenge.data.remote.response.PayloadTickerResponse
import com.example.cryptochallenge.data.repository.dataSources.LocalBooksDataSource
import com.example.cryptochallenge.data.repository.dataSources.RemoteBooksDataSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BooksRepository(
    private val remoteBooksDataSource: RemoteBooksDataSource,
    private val localBooksDataSource: LocalBooksDataSource
) {

    val availableBooks: LiveData<NetworkResponse<List<Book>>> = Transformations.map(
        remoteBooksDataSource.getAllBooks()
    ) {
        it
    }

    fun requestBooks(): Single<List<Book>> {
        return remoteBooksDataSource.fetchAllBooks()
    }

    fun getLocalBooks(): Flowable<List<Book>> {
        return localBooksDataSource.getAllBooks()
    }

    fun saveList(books: List<BookEntity>): Single<Unit> {
        return localBooksDataSource.insertBooks(books)
    }
}