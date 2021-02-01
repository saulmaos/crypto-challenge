package com.example.cryptochallenge.ui.mainAcitivity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.cryptochallenge.R
import com.example.cryptochallenge.RxSchedulerRule
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.repository.BooksRepository
import com.example.cryptochallenge.ui.mainActivity.MainViewModel
import com.example.cryptochallenge.utils.Event
import com.example.cryptochallenge.utils.connectivity.NetworkHelper
import com.google.common.truth.Truth.assertThat
import com.jraska.livedata.test
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var networkHelper: NetworkHelper

    @Mock
    private lateinit var booksRepository: BooksRepository

    private lateinit var viewModel: MainViewModel

    private val book: Book by lazy {
        Book("btc_mxn", "1", "2", "100000"
            , "20000", "10500", "19500", "url")
    }

    @Before
    fun setup() {
        viewModel = MainViewModel(booksRepository, networkHelper, CompositeDisposable())
    }

    @Test
    fun `given no internet and no local books when onInitialRequest should return noDataFound`() {
        val booksError = Throwable("Error getting local Books - IT'S A TEST")
        val internetError = Throwable("Error no internet detected - IT'S A TEST")
        val isThereInternet = false

        `when`(networkHelper.observable())
            .thenReturn(Observable.error(internetError))
        `when`(networkHelper.isNetworkConnected())
            .thenReturn(isThereInternet)
        `when`(booksRepository.getLocalBooks())
            .thenReturn(Maybe.error(booksError))

        val liveObserver = viewModel.events.test()
            .assertNoValue()

        viewModel.onInitialRequest()

        networkHelper.observable().test()
            .assertError(internetError)
            .dispose()

        assertThat(networkHelper.isNetworkConnected()).isFalse()

        booksRepository.getLocalBooks().test()
            .assertError(booksError)
            .dispose()

        liveObserver
            .assertHasValue()
            .assertValueHistory(
                Event(MainViewModel.MainNavigation.ShowBooksListLoading),
                Event(MainViewModel.MainNavigation.NoDataFound),
                Event(MainViewModel.MainNavigation.HideBooksListLoading)
            )
            .assertHistorySize(3)
    }

    @Test
    fun `given no internet and empty local books when onInitialRequest should return noDataFound`() {
        val books = emptyList<Book>()
        val internetError = Throwable("Error no internet detected - IT'S A TEST")
        `when`(networkHelper.observable())
            .thenReturn(Observable.error(internetError))
        `when`(networkHelper.isNetworkConnected())
            .thenReturn(false)
        `when`(booksRepository.getLocalBooks())
            .thenReturn(Maybe.just(books))

        val liveObserver = viewModel.events
            .test()
            .assertNoValue()

        viewModel.onInitialRequest()

        networkHelper.observable().test()
            .assertError(internetError)
            .dispose()

        assertThat(networkHelper.isNetworkConnected()).isFalse()

        booksRepository.getLocalBooks().test()
            .assertValue(books)
            .dispose()

        liveObserver
            .assertHasValue()
            .assertValueHistory(
                Event(MainViewModel.MainNavigation.ShowBooksListLoading),
                Event(MainViewModel.MainNavigation.NoDataFound),
                Event(MainViewModel.MainNavigation.HideBooksListLoading)
            )
            .assertHistorySize(3)
    }

    @Test
    fun `given no internet and correct local books when onInitialRequest should return the books list from local`() {
        val books = listOf(book)
        val internetError = Throwable("Error no internet detected - IT'S A TEST")
        `when`(networkHelper.observable())
            .thenReturn(Observable.error(internetError))
        `when`(networkHelper.isNetworkConnected())
            .thenReturn(false)
        `when`(booksRepository.getLocalBooks())
            .thenReturn(Maybe.just(books))

        val liveObserver = viewModel.events
            .test()
            .assertNoValue()

        viewModel.onInitialRequest()

        networkHelper.observable().test()
            .assertError(internetError)
            .dispose()

        assertThat(networkHelper.isNetworkConnected()).isFalse()

        booksRepository.getLocalBooks().test()
            .assertValue(books)
            .dispose()

        liveObserver
            .assertHasValue()
            .assertValueHistory(
                Event(MainViewModel.MainNavigation.ShowBooksListLoading),
                Event(MainViewModel.MainNavigation.BooksList(books)),
                Event(MainViewModel.MainNavigation.HideBooksListLoading)
            )
            .assertHistorySize(3)
    }

    @Test
    fun `given valid internet and valid retrofit response when onInitialRequest should show the books list from server`() {
        val books = listOf(book)
        val thereIsInternet = true

        `when`(networkHelper.observable())
            .thenReturn(Observable.just(thereIsInternet))
        `when`(networkHelper.isNetworkConnected())
            .thenReturn(thereIsInternet)
        `when`(booksRepository.requestBooks())
            .thenReturn(Single.just(books))
        `when`(booksRepository.saveList(books))
            .thenReturn(Single.just(Unit))

        val liveObserver = viewModel.events.test()
            .assertNoValue()

        viewModel.onInitialRequest()

        networkHelper.observable().test()
            .assertValue(thereIsInternet)
            .dispose()

        assertThat(networkHelper.isNetworkConnected()).isTrue()

        booksRepository.requestBooks().test()
            .assertValue(books)
            .dispose()

        booksRepository.saveList(books).test()
            .assertValue(Unit)
            .dispose()

        liveObserver
            .assertHasValue()
            .assertValueHistory(
                Event(MainViewModel.MainNavigation.ShowBooksListLoading),
                Event(MainViewModel.MainNavigation.BooksList(books)),
                Event(MainViewModel.MainNavigation.HideBooksListLoading)
            )
            .assertHistorySize(3)
    }

    @Test
    fun `given valid internet and no valid retrofit response when onInitialRequest should show the books list from local`() {
        val books = listOf(book)
        val thereIsInternet = true
        val retrofitError = Throwable("Error with Retrofit - IT'S A TEST")

        `when`(networkHelper.observable())
            .thenReturn(Observable.just(thereIsInternet))
        `when`(networkHelper.isNetworkConnected())
            .thenReturn(thereIsInternet)
        `when`(booksRepository.requestBooks())
            .thenReturn(Single.error(retrofitError))
        `when`(booksRepository.getLocalBooks())
            .thenReturn(Maybe.just(books))

        val liveObserver = viewModel.events.test()
            .assertNoValue()

        viewModel.onInitialRequest()

        networkHelper.observable().test()
            .assertValue(thereIsInternet)
            .dispose()

        assertThat(networkHelper.isNetworkConnected()).isTrue()

        booksRepository.requestBooks().test()
            .assertError(retrofitError)
            .dispose()

        booksRepository.getLocalBooks().test()
            .assertValue(books)
            .dispose()

        liveObserver
            .assertHasValue()
            .assertValueHistory(
                Event(MainViewModel.MainNavigation.ShowBooksListLoading),
                Event(MainViewModel.MainNavigation.Error(R.string.error_on_request_books)),
                Event(MainViewModel.MainNavigation.BooksList(books)),
                Event(MainViewModel.MainNavigation.HideBooksListLoading)
            )
            .assertHistorySize(4)
    }

    @Test
    fun `given no internet and correct local books when onReload should return the books list from local`() {
        val books = listOf(book)
        val isThereInternet = false

        `when`(networkHelper.isNetworkConnected())
            .thenReturn(isThereInternet)
        `when`(booksRepository.getLocalBooks())
            .thenReturn(Maybe.just(books))

        val liveObserver = viewModel.events
            .test()
            .assertNoValue()

        viewModel.onReload()

        assertThat(networkHelper.isNetworkConnected()).isFalse()

        booksRepository.getLocalBooks().test()
            .assertValue(books)
            .dispose()

        liveObserver
            .assertHasValue()
            .assertValueHistory(
                Event(MainViewModel.MainNavigation.Error(R.string.error_no_internet)),
                Event(MainViewModel.MainNavigation.HideBooksListLoading)
            )
            .assertHistorySize(2)
    }

    @Test
    fun `given valid internet and valid retrofit response when onReload should show the books list from server`() {
        val books = listOf(book)
        val thereIsInternet = true

        `when`(networkHelper.isNetworkConnected())
            .thenReturn(thereIsInternet)
        `when`(booksRepository.requestBooks())
            .thenReturn(Single.just(books))
        `when`(booksRepository.saveList(books))
            .thenReturn(Single.just(Unit))

        val liveObserver = viewModel.events.test()
            .assertNoValue()

        viewModel.onReload()

        assertThat(networkHelper.isNetworkConnected()).isTrue()

        booksRepository.requestBooks().test()
            .assertValue(books)
            .dispose()

        booksRepository.saveList(books).test()
            .assertValue(Unit)
            .dispose()

        liveObserver
            .assertHasValue()
            .assertValueHistory(
                Event(MainViewModel.MainNavigation.ShowBooksListLoading),
                Event(MainViewModel.MainNavigation.BooksList(books)),
                Event(MainViewModel.MainNavigation.HideBooksListLoading)
            )
            .assertHistorySize(3)
    }
}