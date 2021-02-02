package com.example.cryptochallenge.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PayloadTickerResponse (
    @Expose
    @SerializedName("book")
    var book: String,

    @Expose
    @SerializedName("volume")
    var volume: String,

    @Expose
    @SerializedName("high")
    var high: String,

    @Expose
    @SerializedName("last")
    var last: String,

    @Expose
    @SerializedName("low")
    var low: String,

    @Expose
    @SerializedName("vwap")
    var vwap: String,

    @Expose
    @SerializedName("ask")
    var ask: String,

    @Expose
    @SerializedName("bid")
    var bid: String,

    @Expose
    @SerializedName("created_at")
    var createdAt: String
)