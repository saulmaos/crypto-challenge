package com.example.cryptochallenge.data.repository.dataSources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.model.Order
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.data.remote.response.BaseResponse
import com.example.cryptochallenge.data.remote.response.PayloadOrderBookResponse
import com.example.cryptochallenge.data.repository.NetworkResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderRetrofitDataSource(
    private val networkService: NetworkService
): RemoteOrderDataSource {
    private val orderBook: MutableLiveData<NetworkResponse<OrderBook>> = MutableLiveData()

    override fun fetchOrderBooks(book: String) {
        val call = networkService.doOrderBookCall(book)
        call.enqueue(
            object : Callback<BaseResponse<PayloadOrderBookResponse>> {
                override fun onFailure(
                    call: Call<BaseResponse<PayloadOrderBookResponse>>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                    orderBook.value = NetworkResponse.Error(R.string.error_on_request_order_book)
                }

                override fun onResponse(
                    call: Call<BaseResponse<PayloadOrderBookResponse>>,
                    response: Response<BaseResponse<PayloadOrderBookResponse>>
                ) {
                    if (!response.body()!!.success) {
                        orderBook.value = NetworkResponse.Error(R.string.error_on_request_order_book)
                        return
                    }
                    val bids: List<Order> = response.body()?.payload?.bids!!.map {
                        Order(it.book, it.price, it.amount)
                    }
                    val asks: List<Order> = response.body()?.payload?.asks!!.map {
                        Order(it.book, it.price, it.amount)
                    }
                    orderBook.value = NetworkResponse.Success(OrderBook(asks, bids))
                }
            }
        )
    }

    override fun getLastOrderBook(): LiveData<NetworkResponse<OrderBook>> {
        return orderBook
    }
}