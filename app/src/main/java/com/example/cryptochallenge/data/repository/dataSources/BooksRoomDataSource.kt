package com.example.cryptochallenge.data.repository.dataSources

import com.example.cryptochallenge.data.local.dao.BookDao
import com.example.cryptochallenge.data.local.entity.BookEntity
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.utils.toBookEntityList
import com.example.cryptochallenge.utils.toBookList
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class BooksRoomDataSource(
    private val bookDao: BookDao
) : LocalBooksDataSource {
    override fun getAllBooks(): Maybe<List<Book>> {
        return bookDao.getAllBooks()
            .map(List<BookEntity>::toBookList)
            .subscribeOn(Schedulers.io())
    }

    override fun insertBooks(books: List<Book>): Single<Unit> {
        return bookDao.insertMany(books.toBookEntityList())
            .subscribeOn(Schedulers.io())
    }
}