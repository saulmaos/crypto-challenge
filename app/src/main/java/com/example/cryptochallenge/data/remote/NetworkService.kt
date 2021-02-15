package com.example.cryptochallenge.data.remote

import com.example.cryptochallenge.data.remote.response.BaseResponse
import com.example.cryptochallenge.data.remote.response.PayloadAvailableBookResponse
import com.example.cryptochallenge.data.remote.response.PayloadOrderBookResponse
import com.example.cryptochallenge.data.remote.response.PayloadTickerResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    @GET(AVAILABLE_BOOKS)
    fun doAvailableBooksCall(): Single<BaseResponse<List<PayloadAvailableBookResponse>>>

    @GET(TICKER)
    suspend fun doTickerCall(@Query("book") book: String): BaseResponse<PayloadTickerResponse>

    @GET(ORDER_BOOK)
    suspend fun doOrderBookCall(@Query("book") book: String): BaseResponse<PayloadOrderBookResponse>
}