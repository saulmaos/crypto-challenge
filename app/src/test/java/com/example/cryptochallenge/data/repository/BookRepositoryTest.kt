package com.example.cryptochallenge.data.repository

import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.repository.dataSources.LocalBooksDataSource
import com.example.cryptochallenge.data.repository.dataSources.RemoteBooksDataSource
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BookRepositoryTest {

    @Mock
    private lateinit var remoteBooksDataSource: RemoteBooksDataSource

    @Mock
    private lateinit var localBooksDataSource: LocalBooksDataSource

    private lateinit var booksRepository: BooksRepository

    private val book: Book by lazy {
        Book("btc_mxn", "1", "2", "100000"
            , "20000", "10500", "19500", "url")
    }

    @Before
    fun setup() {
        booksRepository = BooksRepository(remoteBooksDataSource, localBooksDataSource)
    }

    @Test
    fun `test getAllBooks`() {
        val books = listOf(book)

        `when`(localBooksDataSource.getAllBooks())
            .thenReturn(Maybe.just(books))

        booksRepository.getLocalBooks()

        verify(localBooksDataSource).getAllBooks()
    }

    @Test
    fun `test insertBooks`() {
        val books = listOf(book)

        `when`(localBooksDataSource.insertBooks(books))
            .thenReturn(Single.just(Unit))

        booksRepository.saveList(books)

        verify(localBooksDataSource).insertBooks(books)
    }

    @Test
    fun `test fetchAllBooks`() {
        val books = listOf(book)

        `when`(remoteBooksDataSource.fetchAllBooks())
            .thenReturn(Single.just(books))

        booksRepository.requestBooks()

        verify(remoteBooksDataSource).fetchAllBooks()
    }
}