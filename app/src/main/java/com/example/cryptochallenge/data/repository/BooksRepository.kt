package com.example.cryptochallenge.data.repository

import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.repository.dataSources.LocalBooksDataSource
import com.example.cryptochallenge.data.repository.dataSources.RemoteBooksDataSource
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class BooksRepository @Inject constructor(
    private val remoteBooksDataSource: RemoteBooksDataSource,
    private val localBooksDataSource: LocalBooksDataSource
) {
    fun requestBooks(): Single<List<Book>> {
        return remoteBooksDataSource.fetchAllBooks()
    }

    fun getLocalBooks(): Maybe<List<Book>> {
        return localBooksDataSource.getAllBooks()
    }

    fun saveList(books: List<Book>): Single<Unit> {
        return localBooksDataSource.insertBooks(books)
    }
}