package com.example.cryptochallenge.data.model

data class OrderBook(
    var asks: List<Order>,

    var bids: List<Order>
)

data class Order(
    var book: String,

    var price: String,

    var amount: String
)