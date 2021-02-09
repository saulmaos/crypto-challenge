package com.example.cryptochallenge.data.repository.dataSources

import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.data.remote.response.PayloadAvailableBookResponse
import com.example.cryptochallenge.utils.toBook
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class BooksRetrofitDataSource @Inject constructor(
    private val networkService: NetworkService
) : RemoteBooksDataSource {

    override fun fetchAllBooks(): Single<List<Book>> {

        return networkService.doAvailableBooksCall()
            .map {
                it.payload.map(PayloadAvailableBookResponse::toBook)
            }
            .subscribeOn(Schedulers.io())
    }
}