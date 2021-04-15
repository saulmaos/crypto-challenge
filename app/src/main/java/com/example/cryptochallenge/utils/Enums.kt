package com.example.cryptochallenge.utils

enum class OrderType(val type: String) {
    BIDS("Bids"),
    ASKS("Asks")
}

enum class PriceChange {
    CURRENT_IS_HIGHER,
    CURRENT_IS_LOWER,
    NO_CHANGE
}