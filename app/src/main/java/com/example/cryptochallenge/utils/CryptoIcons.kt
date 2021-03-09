package com.example.cryptochallenge.utils

fun createUrlByBook(book: String): String {
    val ticker = book.substring(0, book.indexOf("_"))
    return createUrlByTicker(ticker = ticker)
}

fun createUrlByTicker(ticker: String): String {
    return "https://cdn.jsdelivr.net/gh/atomiclabs/cryptocurrency-icons@9ab8d6934b83a4aa8ae5e8711609a70ca0ab1b2b/128/color/$ticker.png"
}