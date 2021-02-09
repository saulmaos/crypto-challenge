package com.example.cryptochallenge.ui.detailAcitivity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.cryptochallenge.MainCoroutineScopeRule
import com.example.cryptochallenge.R
import com.example.cryptochallenge.RxSchedulerRule
import com.example.cryptochallenge.data.model.Order
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.repository.CoinDetailsRepository
import com.example.cryptochallenge.ui.detailFragment.DetailViewModel
import com.example.cryptochallenge.utils.Event
import com.example.cryptochallenge.utils.connectivity.NetworkHelper
import com.google.common.truth.Truth.assertThat
import com.jraska.livedata.test
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import kotlin.RuntimeException

@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var networkHelper: NetworkHelper

    @Mock
    private lateinit var coinDetailsRepository: CoinDetailsRepository

    private lateinit var viewModel: DetailViewModel

    private val book: String by lazy {
        "btc_mxn"
    }

    private val ticker: Ticker by lazy {
        Ticker(book, "1", "1", "1", "1", "1", "1", "1", "1")
    }

    private val orderBook: OrderBook by lazy {
        val bids = listOf(Order(book, "1", "1"))
        val asks = listOf(Order(book, "1", "1"))
        OrderBook(asks, bids)
    }

    @Before
    fun setup() {
        viewModel = DetailViewModel(coinDetailsRepository, networkHelper, CompositeDisposable())
    }

    @Test
    fun `given no internet and no local ticker and orderBook when onInitialRequest should show error msg`() {
        val internetError = Throwable("Error no internet detected - IT'S A TEST")
        val tickerError = Throwable("Error getting Ticker from Local - IT'S A TEST")
        val orderBookError = Throwable("Error getting Ticker from Local - IT'S A TEST")
        val isThereInternet = false
        val params = book.split("_")

        `when`(networkHelper.observable())
            .thenReturn(Observable.error(internetError))
        `when`(networkHelper.isNetworkConnected())
            .thenReturn(isThereInternet)
        `when`(coinDetailsRepository.getLocalTicker(book))
            .thenReturn(Single.error(tickerError))
        `when`(coinDetailsRepository.getLocalOrderBook(book))
            .thenReturn(Single.error(orderBookError))

        val eventObserver = viewModel.events.test()
            .assertNoValue()

        val pairObserver = viewModel.pair.test()
            .assertNoValue()

        viewModel.onInitialRequest(book)

        networkHelper.observable().test()
            .assertError(internetError)
            .dispose()

        assertThat(networkHelper.isNetworkConnected()).isFalse()

        coinDetailsRepository.getLocalTicker(book).test()
            .assertError(tickerError)
            .dispose()

        coinDetailsRepository.getLocalOrderBook(book).test()
            .assertError(orderBookError)
            .dispose()

        eventObserver
            .assertHasValue()
            .assertValueHistory(
                Event(DetailViewModel.DetailNavigation.ShowLoading),
                Event(DetailViewModel.DetailNavigation.TickerResult(Ticker.defaultTicker())),
                Event(DetailViewModel.DetailNavigation.Error(R.string.error_on_request_data)),
                Event(DetailViewModel.DetailNavigation.Error(R.string.error_on_request_data)),
                Event(DetailViewModel.DetailNavigation.HideLoading)
            )
            .assertHistorySize(5)
        pairObserver
            .assertHasValue()
            .assertValue(Pair(params[0], params[1]))
    }

    @Test
    fun `given no internet and valid local ticker and no local orderBook when onInitialRequest should show error msg`() {
        val internetError = Throwable("Error no internet detected - IT'S A TEST")
        val orderBookError = Throwable("Error getting Ticker from Local - IT'S A TEST")
        val isThereInternet = false

        `when`(networkHelper.observable())
            .thenReturn(Observable.error(internetError))
        `when`(networkHelper.isNetworkConnected())
            .thenReturn(isThereInternet)
        `when`(coinDetailsRepository.getLocalTicker(book))
            .thenReturn(Single.just(ticker))
        `when`(coinDetailsRepository.getLocalOrderBook(book))
            .thenReturn(Single.error(orderBookError))

        val eventObserver = viewModel.events.test()
            .assertNoValue()

        viewModel.onInitialRequest(book)

        networkHelper.observable().test()
            .assertError(internetError)
            .dispose()

        assertThat(networkHelper.isNetworkConnected()).isFalse()

        coinDetailsRepository.getLocalTicker(book).test()
            .assertValue(ticker)
            .dispose()

        coinDetailsRepository.getLocalOrderBook(book).test()
            .assertError(orderBookError)
            .dispose()

        eventObserver
            .assertHasValue()
            .assertValueHistory(
                Event(DetailViewModel.DetailNavigation.ShowLoading),
                Event(DetailViewModel.DetailNavigation.TickerResult(ticker)),
                Event(DetailViewModel.DetailNavigation.Error(R.string.error_on_request_data)),
                Event(DetailViewModel.DetailNavigation.HideLoading)
            )
            .assertHistorySize(4)
    }

    @Test
    fun `given no internet and no local ticker and valid local orderBook when onInitialRequest should show error msg`() {
        val internetError = Throwable("Error no internet detected - IT'S A TEST")
        val tickerError = Throwable("Error getting Ticker from Local - IT'S A TEST")
        val isThereInternet = false

        `when`(networkHelper.observable())
            .thenReturn(Observable.error(internetError))
        `when`(networkHelper.isNetworkConnected())
            .thenReturn(isThereInternet)
        `when`(coinDetailsRepository.getLocalTicker(book))
            .thenReturn(Single.error(tickerError))
        `when`(coinDetailsRepository.getLocalOrderBook(book))
            .thenReturn(Single.just(orderBook))

        val eventObserver = viewModel.events.test()
            .assertNoValue()

        viewModel.onInitialRequest(book)

        networkHelper.observable().test()
            .assertError(internetError)
            .dispose()

        assertThat(networkHelper.isNetworkConnected()).isFalse()

        coinDetailsRepository.getLocalTicker(book).test()
            .assertError(tickerError)
            .dispose()

        coinDetailsRepository.getLocalOrderBook(book).test()
            .assertValue(orderBook)
            .dispose()

        eventObserver
            .assertHasValue()
            .assertValueHistory(
                Event(DetailViewModel.DetailNavigation.ShowLoading),
                Event(DetailViewModel.DetailNavigation.TickerResult(Ticker.defaultTicker())),
                Event(DetailViewModel.DetailNavigation.Error(R.string.error_on_request_data)),
                Event(DetailViewModel.DetailNavigation.OrderBookResult(orderBook)),
                Event(DetailViewModel.DetailNavigation.HideLoading)
            )
            .assertHistorySize(5)
    }

    //
    @Test
    fun `given no internet and valid local ticker and orderBook when onInitialRequest should show the data`() {
        val internetError = Throwable("Error no internet detected - IT'S A TEST")
        val isThereInternet = false

        `when`(networkHelper.observable())
            .thenReturn(Observable.error(internetError))
        `when`(networkHelper.isNetworkConnected())
            .thenReturn(isThereInternet)
        `when`(coinDetailsRepository.getLocalTicker(book))
            .thenReturn(Single.just(ticker))
        `when`(coinDetailsRepository.getLocalOrderBook(book))
            .thenReturn(Single.just(orderBook))

        val eventObserver = viewModel.events.test()
            .assertNoValue()

        viewModel.onInitialRequest(book)

        networkHelper.observable().test()
            .assertError(internetError)
            .dispose()

        assertThat(networkHelper.isNetworkConnected()).isFalse()

        coinDetailsRepository.getLocalTicker(book).test()
            .assertValue(ticker)
            .dispose()

        coinDetailsRepository.getLocalOrderBook(book).test()
            .assertValue(orderBook)
            .dispose()

        eventObserver
            .assertHasValue()
            .assertValueHistory(
                Event(DetailViewModel.DetailNavigation.ShowLoading),
                Event(DetailViewModel.DetailNavigation.TickerResult(ticker)),
                Event(DetailViewModel.DetailNavigation.OrderBookResult(orderBook)),
                Event(DetailViewModel.DetailNavigation.HideLoading)
            )
            .assertHistorySize(4)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `given valid internet and valid server ticker and orderBook when onInitialRequest should show the data`() =
        runBlockingTest {
            val isThereInternet = true

            `when`(networkHelper.observable())
                .thenReturn(Observable.just(isThereInternet))
            `when`(networkHelper.isNetworkConnected())
                .thenReturn(isThereInternet)
            `when`(coinDetailsRepository.requestTicker(book))
                .thenReturn(ticker)
            `when`(coinDetailsRepository.saveTicker(ticker))
                .thenReturn(Unit)
            `when`(coinDetailsRepository.requestOrderBook(book))
                .thenReturn(orderBook)
            `when`(coinDetailsRepository.deleteOrderBook(book))
                .thenReturn(Unit)
            `when`(coinDetailsRepository.saveOrderBook(orderBook))
                .thenReturn(Unit)

            val eventObserver = viewModel.events.test()
                .assertNoValue()

            viewModel.onInitialRequest(book)

            networkHelper.observable().test()
                .assertValue(isThereInternet)
                .dispose()

            assertThat(networkHelper.isNetworkConnected()).isTrue()

//        coinDetailsRepository.requestTicker(book).test()
//            .assertValue(ticker)
//            .dispose()
//
//        coinDetailsRepository.requestOrderBook(book).test()
//            .assertValue(orderBook)
//            .dispose()

            eventObserver
                .assertHasValue()
                .assertValueHistory(
                    Event(DetailViewModel.DetailNavigation.ShowLoading),
                    Event(DetailViewModel.DetailNavigation.TickerResult(ticker)),
                    Event(DetailViewModel.DetailNavigation.OrderBookResult(orderBook)),
                    Event(DetailViewModel.DetailNavigation.HideLoading)
                )
                .assertHistorySize(4)
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `given valid internet and valid local ticker and valid server orderBook when onInitialRequest should show the data`() =
        runBlockingTest {
            val tickerError = RuntimeException("Error fetching Ticker from server - IT'S A TEST")
            val isThereInternet = true

            `when`(networkHelper.observable())
                .thenReturn(Observable.just(isThereInternet))
            `when`(networkHelper.isNetworkConnected())
                .thenReturn(isThereInternet)

            `when`(coinDetailsRepository.requestTicker(book))
                .thenThrow(tickerError)

            `when`(coinDetailsRepository.getLocalTicker(book))
                .thenReturn(Single.just(ticker))

            `when`(coinDetailsRepository.requestOrderBook(book))
                .thenReturn(orderBook)
            `when`(coinDetailsRepository.deleteOrderBook(book))
                .thenReturn(Unit)
            `when`(coinDetailsRepository.saveOrderBook(orderBook))
                .thenReturn(Unit)

            val eventObserver = viewModel.events.test()
                .assertNoValue()

            viewModel.onInitialRequest(book)

            networkHelper.observable().test()
                .assertValue(isThereInternet)
                .dispose()

            assertThat(networkHelper.isNetworkConnected()).isTrue()

            verify(coinDetailsRepository).requestTicker(book)

            coinDetailsRepository.getLocalTicker(book).test()
                .assertValue(ticker)
                .dispose()

            verify(coinDetailsRepository).requestOrderBook(book)
            verify(coinDetailsRepository).deleteOrderBook(book)
            verify(coinDetailsRepository).saveOrderBook(orderBook)

            eventObserver
                .assertHasValue()
                .assertValueHistory(
                    Event(DetailViewModel.DetailNavigation.ShowLoading),
                    Event(DetailViewModel.DetailNavigation.TickerResult(ticker)),
                    Event(DetailViewModel.DetailNavigation.OrderBookResult(orderBook)),
                    Event(DetailViewModel.DetailNavigation.HideLoading)
                )
                .assertHistorySize(4)
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `given valid internet and valid server ticker and valid local orderBook when onInitialRequest should show the data`() =
        runBlockingTest {
            val orderBookError =
                RuntimeException("Error fetching OrderBook from server - IT'S A TEST")
            val isThereInternet = true

            `when`(networkHelper.observable())
                .thenReturn(Observable.just(isThereInternet))
            `when`(networkHelper.isNetworkConnected())
                .thenReturn(isThereInternet)

            `when`(coinDetailsRepository.requestTicker(book))
                .thenReturn(ticker)
            `when`(coinDetailsRepository.saveTicker(ticker))
                .thenReturn(Unit)

            `when`(coinDetailsRepository.requestOrderBook(book))
                .thenThrow(orderBookError)
            `when`(coinDetailsRepository.getLocalOrderBook(book))
                .thenReturn(Single.just(orderBook))

            val eventObserver = viewModel.events.test()
                .assertNoValue()

            viewModel.onInitialRequest(book)

            networkHelper.observable().test()
                .assertValue(isThereInternet)
                .dispose()

            assertThat(networkHelper.isNetworkConnected()).isTrue()

            verify(coinDetailsRepository).requestTicker(book)
            verify(coinDetailsRepository).saveTicker(ticker)
            verify(coinDetailsRepository).requestOrderBook(book)

            coinDetailsRepository.getLocalOrderBook(book).test()
                .assertValue(orderBook)
                .dispose()

            eventObserver
                .assertHasValue()
                .assertValueHistory(
                    Event(DetailViewModel.DetailNavigation.ShowLoading),
                    Event(DetailViewModel.DetailNavigation.TickerResult(ticker)),
                    Event(DetailViewModel.DetailNavigation.OrderBookResult(orderBook)),
                    Event(DetailViewModel.DetailNavigation.HideLoading)
                )
                .assertHistorySize(4)
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `given valid internet and valid local ticker and orderBook when onInitialRequest should show the data`() =
        runBlockingTest {
            val tickerError = RuntimeException("Error fetching Ticker from server - IT'S A TEST")
            val orderBookError = RuntimeException("Error fetching OrderBook from server - IT'S A TEST")
            val isThereInternet = true

            `when`(networkHelper.observable())
                .thenReturn(Observable.just(isThereInternet))
            `when`(networkHelper.isNetworkConnected())
                .thenReturn(isThereInternet)

            `when`(coinDetailsRepository.requestTicker(book))
                .thenThrow(tickerError)
            `when`(coinDetailsRepository.getLocalTicker(book))
                .thenReturn(Single.just(ticker))

            `when`(coinDetailsRepository.requestOrderBook(book))
                .thenThrow(orderBookError)
            `when`(coinDetailsRepository.getLocalOrderBook(book))
                .thenReturn(Single.just(orderBook))

            val eventObserver = viewModel.events.test()
                .assertNoValue()

            viewModel.onInitialRequest(book)

            networkHelper.observable().test()
                .assertValue(isThereInternet)
                .dispose()
            assertThat(networkHelper.isNetworkConnected()).isTrue()

            verify(coinDetailsRepository).requestTicker(book)
            coinDetailsRepository.getLocalTicker(book).test()
                .assertValue(ticker)
                .dispose()

            verify(coinDetailsRepository).requestOrderBook(book)
            coinDetailsRepository.getLocalOrderBook(book).test()
                .assertValue(orderBook)
                .dispose()

            eventObserver
                .assertHasValue()
                .assertValueHistory(
                    Event(DetailViewModel.DetailNavigation.ShowLoading),
                    Event(DetailViewModel.DetailNavigation.TickerResult(ticker)),
                    Event(DetailViewModel.DetailNavigation.OrderBookResult(orderBook)),
                    Event(DetailViewModel.DetailNavigation.HideLoading)
                )
                .assertHistorySize(4)
        }

    @Test
    fun `given no internet when onReloadPressed should show no internet msg`() {
        val isThereInternet = false

        `when`(networkHelper.isNetworkConnected())
            .thenReturn(isThereInternet)

        val eventObserver = viewModel.events.test()
            .assertNoValue()

        viewModel.onReloadPressed(book)

        assertThat(networkHelper.isNetworkConnected()).isFalse()

        eventObserver.assertValue(Event(DetailViewModel.DetailNavigation.Error(R.string.error_no_internet)))
            .assertHistorySize(1)
    }
}
