package com.example.cryptochallenge.data.remote

import com.example.cryptochallenge.data.remote.response.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    @GET(AVAILABLE_BOOKS)
    fun doAvailableBooksCall(): Call<BaseResponse<List<PayloadAvailableBookResponse>>>

    @GET(TICKER)
    fun doTickerCall(@Query("book") book: String): Call<BaseResponse<PayloadTickerResponse>>

    @GET(ORDER_BOOK)
    fun doOrderBookCall(@Query("book") book: String): Call<BaseResponse<PayloadOrderBookResponse>>

}