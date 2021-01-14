package com.example.cryptochallenge.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @Expose
    @SerializedName("success")
    var success: Boolean,

    @Expose
    @SerializedName("payload")
    var payload: T
)