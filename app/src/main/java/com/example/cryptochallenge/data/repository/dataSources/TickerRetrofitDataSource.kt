package com.example.cryptochallenge.data.repository.dataSources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.data.remote.response.BaseResponse
import com.example.cryptochallenge.data.remote.response.PayloadTickerResponse
import com.example.cryptochallenge.data.repository.NetworkResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TickerRetrofitDataSource(
    private val networkService: NetworkService
): RemoteTickerDataSource {
    private val ticker: MutableLiveData<NetworkResponse<Ticker>> = MutableLiveData()

    override fun fetchTicker(book: String) {
        val call = networkService.doTickerCall(book)
        call.enqueue(
            object : Callback<BaseResponse<PayloadTickerResponse>> {
                override fun onFailure(
                    call: Call<BaseResponse<PayloadTickerResponse>>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                    ticker.value = NetworkResponse.Error(R.string.error_on_request_ticker)
                }

                override fun onResponse(
                    call: Call<BaseResponse<PayloadTickerResponse>>,
                    response: Response<BaseResponse<PayloadTickerResponse>>
                ) {
                    if (!response.body()!!.success) {
                        ticker.value = NetworkResponse.Error(R.string.error_on_request_ticker)
                        return
                    }
                    ticker.value = NetworkResponse.Success(
                        response.body()?.payload!!.let {
                            Ticker(
                                it.book,
                                it.volume,
                                it.high,
                                it.last,
                                it.low,
                                it.vwap,
                                it.ask,
                                it.bid,
                                it.createdAt
                            )
                        }
                    )
                }
            }
        )
    }

    override fun getTicker(): LiveData<NetworkResponse<Ticker>> {
        return ticker
    }
}