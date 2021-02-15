package com.example.cryptochallenge.data.remote

import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.remote.FakeNetworkServiceConfig.booksResponse
import com.example.cryptochallenge.data.remote.FakeNetworkServiceConfig.orderBookResponse
import com.example.cryptochallenge.data.remote.FakeNetworkServiceConfig.tickerResponse
import com.example.cryptochallenge.data.remote.response.BaseResponse
import com.example.cryptochallenge.data.remote.response.PayloadAvailableBookResponse
import com.example.cryptochallenge.data.remote.response.PayloadOrderBookResponse
import com.example.cryptochallenge.data.remote.response.PayloadTickerResponse
import com.example.cryptochallenge.utils.toPayloadAvailableBookResponseList
import com.example.cryptochallenge.utils.toPayloadOrderBookResponse
import com.example.cryptochallenge.utils.toPayloadTickerResponse
import io.reactivex.Single

class FakeNetworkService : NetworkService {
    override fun doAvailableBooksCall(): Single<BaseResponse<List<PayloadAvailableBookResponse>>> {
        return Single.just(booksResponse)
    }

    override suspend fun doTickerCall(book: String): BaseResponse<PayloadTickerResponse> {
        return tickerResponse!!
    }

    override suspend fun doOrderBookCall(book: String): BaseResponse<PayloadOrderBookResponse> {
        return orderBookResponse!!
    }
}

object FakeNetworkServiceConfig {
    var booksResponse: BaseResponse<List<PayloadAvailableBookResponse>>? = null
    var tickerResponse: BaseResponse<PayloadTickerResponse>? = null
    var orderBookResponse: BaseResponse<PayloadOrderBookResponse>? = null

    fun setBooks(books: List<Book>) {
        val payloadList: List<PayloadAvailableBookResponse> =
            books.toPayloadAvailableBookResponseList()
        booksResponse = BaseResponse(true, payload = payloadList)
    }

    fun setTicker(ticker: Ticker) {
        val payloadTicker: PayloadTickerResponse = ticker.toPayloadTickerResponse()
        tickerResponse = BaseResponse(true, payloadTicker)
    }

    fun setOrderBook(orderBook: OrderBook) {
        val payloadOrderBook: PayloadOrderBookResponse = orderBook.toPayloadOrderBookResponse()
        orderBookResponse = BaseResponse(true, payloadOrderBook)
    }
}