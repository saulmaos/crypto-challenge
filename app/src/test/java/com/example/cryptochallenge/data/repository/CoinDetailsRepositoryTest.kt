package com.example.cryptochallenge.data.repository

import com.example.cryptochallenge.data.model.Order
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.repository.dataSources.LocalOrderBookDataSource
import com.example.cryptochallenge.data.repository.dataSources.LocalTickerDataSource
import com.example.cryptochallenge.data.repository.dataSources.RemoteOrderBookDataSource
import com.example.cryptochallenge.data.repository.dataSources.RemoteTickerDataSource
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CoinDetailsRepositoryTest {

    @Mock
    private lateinit var localTickerDataSource: LocalTickerDataSource

    @Mock
    private lateinit var remoteTickerDataSource: RemoteTickerDataSource

    @Mock
    private lateinit var localOrderBookDataSource: LocalOrderBookDataSource

    @Mock
    private lateinit var remoteOrderBookDataSource: RemoteOrderBookDataSource

    private lateinit var coinDetailsRepository: CoinDetailsRepository

    private val book = "btc_mxn"

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
        coinDetailsRepository = CoinDetailsRepository(remoteTickerDataSource, remoteOrderBookDataSource, localTickerDataSource, localOrderBookDataSource)
    }

    @Test
    fun `test fetchTicker`() {
        `when`(remoteTickerDataSource.fetchTicker(book))
            .thenReturn(Single.just(ticker))

        coinDetailsRepository.requestTicker(book)


        verify(remoteTickerDataSource).fetchTicker(book)
    }

    @Test
    fun `test getTicker`() {
        `when`(localTickerDataSource.getTicker(book))
            .thenReturn(Single.just(ticker))

        coinDetailsRepository.getLocalTicker(book)


        verify(localTickerDataSource).getTicker(book)
    }

    @Test
    fun `test insertTicker`() {
        `when`(localTickerDataSource.insertTicker(ticker))
            .thenReturn(Single.just(Unit))

        coinDetailsRepository.saveTicker(ticker)


        verify(localTickerDataSource).insertTicker(ticker)
    }

    @Test
    fun `test fetchOrderBooks`() {
        `when`(remoteOrderBookDataSource.fetchOrderBooks(book))
            .thenReturn(Single.just(orderBook))

        coinDetailsRepository.requestOrderBook(book)


        verify(remoteOrderBookDataSource).fetchOrderBooks(book)
    }

    @Test
    fun `test getOrderBook`() {
        `when`(localOrderBookDataSource.getOrderBook(book))
            .thenReturn(Single.just(orderBook))

        coinDetailsRepository.getLocalOrderBook(book)


        verify(localOrderBookDataSource).getOrderBook(book)
    }

    @Test
    fun `test insertOrderBook`() {
        `when`(localOrderBookDataSource.insertOrderBook(orderBook))
            .thenReturn(Single.just(Unit))

        coinDetailsRepository.saveOrderBook(orderBook)


        verify(localOrderBookDataSource).insertOrderBook(orderBook)
    }

    @Test
    fun `test deleteOrderBookByBook`() {
        `when`(localOrderBookDataSource.deleteOrderBookByBook(book))
            .thenReturn(Single.just(Unit))

        coinDetailsRepository.deleteOrderBook(book)


        verify(localOrderBookDataSource).deleteOrderBookByBook(book)
    }
}