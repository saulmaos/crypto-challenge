package com.example.cryptochallenge.data.model

import java.util.*

data class OrderBook(
    var asks: List<Order>,

    var bids: List<Order>
)

data class Order(
    var book: String,

    var price: String,

    var amount: String
)

fun Order.currency(): String {
    val index = book.indexOf("_")
    if (index == -1) return ""
    return book.substring(index + 1).toUpperCase(Locale.ROOT)
}

fun Order.coin(): String {
    val index = book.indexOf("_")
    if (index == -1) return ""
    return book.substring(0, index).toUpperCase(Locale.ROOT)
}