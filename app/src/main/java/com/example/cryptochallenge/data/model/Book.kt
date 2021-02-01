package com.example.cryptochallenge.data.model

data class Book(
    var book: String,

    var minimumAmount: String,

    var maximumAmount: String,

    var minimumPrice: String,

    var maximumPrice: String,

    var minimumValue: String,

    var maximumValue: String,

    var imageUrl: String,

    var bookPretty: String = book.replace("_", "/")
)