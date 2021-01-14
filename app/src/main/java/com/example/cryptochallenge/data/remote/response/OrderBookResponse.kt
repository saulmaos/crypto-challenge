package com.example.cryptochallenge.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PayloadOrderBookResponse(
    @Expose
    @SerializedName("asks")
    var asks: List<OrderResponse>,

    @Expose
    @SerializedName("bids")
    var bids: List<OrderResponse>,

    @Expose
    @SerializedName("updated_at")
    var updatedAt: String,

    @Expose
    @SerializedName("sequence")
    var sequence: String
)

data class OrderResponse(
    @Expose
    @SerializedName("book")
    var book: String,

    @Expose
    @SerializedName("price")
    var price: String,

    @Expose
    @SerializedName("amount")
    var amount: String
)