package com.example.cryptochallenge.data.model

import java.util.*

data class Ticker(
    var book: String,

    var volume: String,

    var high: String,

    var last: String,

    var low: String,

    var vwap: String,

    var ask: String,

    var bid: String,

    var createdAt: String,

    val currency: String = book.substring(book.indexOf("_") + 1).toUpperCase(Locale.ROOT)
)