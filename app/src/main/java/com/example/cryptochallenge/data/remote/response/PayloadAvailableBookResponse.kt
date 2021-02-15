package com.example.cryptochallenge.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PayloadAvailableBookResponse(
    @Expose
    @SerializedName("book")
    var book: String,

    @Expose
    @SerializedName("minimum_amount")
    var minimumAmount: String,

    @Expose
    @SerializedName("maximum_amount")
    var maximumAmount: String,

    @Expose
    @SerializedName("minimum_price")
    var minimumPrice: String,

    @Expose
    @SerializedName("maximum_price")
    var maximumPrice: String,

    @Expose
    @SerializedName("minimum_value")
    var minimumValue: String,

    @Expose
    @SerializedName("maximum_value")
    var maximumValue: String

)