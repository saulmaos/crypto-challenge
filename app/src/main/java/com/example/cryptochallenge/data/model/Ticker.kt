package com.example.cryptochallenge.data.model

import com.example.cryptochallenge.utils.PriceChange
import java.util.Locale

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

    var lastPriceChangedRegardingThePreviousOne: PriceChange = PriceChange.NO_CHANGE,

    val currency: String = book.substring(book.indexOf("_") + 1).toUpperCase(Locale.ROOT)
) {
    companion object {
        fun defaultTicker() =
            Ticker(
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                PriceChange.NO_CHANGE,
                "",
            )
    }
}