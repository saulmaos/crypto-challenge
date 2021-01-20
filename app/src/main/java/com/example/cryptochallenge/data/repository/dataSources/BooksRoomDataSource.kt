package com.example.cryptochallenge.data.repository.dataSources

import com.example.cryptochallenge.data.local.dao.BookDao
import com.example.cryptochallenge.data.local.entity.BookEntity
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.utils.toBookList
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BooksRoomDataSource(
    private val bookDao: BookDao
) : LocalBooksDataSource {
    override fun getAllBooks(): Flowable<List<Book>> {
        return bookDao.getAllBooks()
            .map(List<BookEntity>::toBookList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun insertBooks(books: List<BookEntity>): Single<Unit> {
        return bookDao.insertMany(books)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}