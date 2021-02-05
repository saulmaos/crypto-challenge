package com.example.cryptochallenge.utils

import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.model.Order
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.remote.response.OrderResponse
import com.example.cryptochallenge.data.remote.response.PayloadAvailableBookResponse
import com.example.cryptochallenge.data.remote.response.PayloadOrderBookResponse
import com.example.cryptochallenge.data.remote.response.PayloadTickerResponse

fun List<Book>.toPayloadAvailableBookResponseList(): List<PayloadAvailableBookResponse> =
    map {
        PayloadAvailableBookResponse(it.book, it.minimumAmount, it.maximumAmount, it.minimumPrice, it.maximumPrice, it.minimumValue, it.maximumValue)
    }

fun Ticker.toPayloadTickerResponse(): PayloadTickerResponse =
    PayloadTickerResponse(book, volume, high, last, low, vwap, ask, bid, createdAt)

fun OrderBook.toPayloadOrderBookResponse(): PayloadOrderBookResponse {
    return PayloadOrderBookResponse(asks.toOrderResponseList(), bids.toOrderResponseList(), "", "")
}

fun List<Order>.toOrderResponseList(): List<OrderResponse> =
    map {
        OrderResponse(it.book, it.price, it.amount)
    }