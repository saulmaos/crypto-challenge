package com.example.cryptochallenge.data.repository

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.model.Order
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.data.remote.response.*
import com.example.cryptochallenge.utils.CryptoIcons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BitsoRepository(
    private val networkService: NetworkService
) {

    private val _availableBooks: MutableLiveData<NetworkResponse> = MutableLiveData()
    val availableBooks: LiveData<NetworkResponse> = _availableBooks

    private val _ticker: MutableLiveData<NetworkResponse> = MutableLiveData()
    val ticker: LiveData<NetworkResponse> = _ticker

    private val _orderBook: MutableLiveData<NetworkResponse> = MutableLiveData()
    val orderBook: LiveData<NetworkResponse> = _orderBook

    fun requestAvailableBooks() {
        val call = networkService.doAvailableBooksCall()
        call.enqueue(
            object : Callback<BaseResponse<List<PayloadAvailableBookResponse>>> {
                override fun onFailure(
                    call: Call<BaseResponse<List<PayloadAvailableBookResponse>>>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                    _availableBooks.value = NetworkResponse.Error(R.string.error_on_request)
                }

                override fun onResponse(
                    call: Call<BaseResponse<List<PayloadAvailableBookResponse>>>,
                    response: Response<BaseResponse<List<PayloadAvailableBookResponse>>>
                ) {
                    if (!response.body()!!.success) {
                        _availableBooks.value = NetworkResponse.Error(R.string.error_on_request)
                        return
                    }
                    _availableBooks.value = NetworkResponse.Success(
                        response.body()?.payload!!.map {
                            Book(
                                it.book,
                                it.minimumAmount,
                                it.maximumAmount,
                                it.minimumPrice,
                                it.maximumPrice,
                                it.minimumValue,
                                it.maximumValue,
                                CryptoIcons.createUrl(it.book)
                            )
                        }
                    )
                }
            }
        )
    }

    fun requestTickerByBook(book: String) {
        val call = networkService.doTickerCall(book)
        call.enqueue(
            object : Callback<BaseResponse<PayloadTickerResponse>> {
                override fun onFailure(
                    call: Call<BaseResponse<PayloadTickerResponse>>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                    _ticker.value = NetworkResponse.Error(R.string.error_on_request_ticker)
                }

                override fun onResponse(
                    call: Call<BaseResponse<PayloadTickerResponse>>,
                    response: Response<BaseResponse<PayloadTickerResponse>>
                ) {
                    if (!response.body()!!.success) {
                        _ticker.value = NetworkResponse.Error(R.string.error_on_request_ticker)
                        return
                    }
                    _ticker.value = NetworkResponse.Success(
                        response.body()?.payload?.let {
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

    fun requestOrdersByBook(book: String) {
        val call = networkService.doOrderBookCall(book)
        call.enqueue(
            object : Callback<BaseResponse<PayloadOrderBookResponse>> {
                override fun onFailure(
                    call: Call<BaseResponse<PayloadOrderBookResponse>>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                    _orderBook.value = NetworkResponse.Error(R.string.error_on_request_book)
                }

                override fun onResponse(
                    call: Call<BaseResponse<PayloadOrderBookResponse>>,
                    response: Response<BaseResponse<PayloadOrderBookResponse>>
                ) {
                    if (!response.body()!!.success) {
                        _orderBook.value = NetworkResponse.Error(R.string.error_on_request_book)
                        return
                    }
                    val bids: List<Order> = response.body()?.payload?.bids!!.map {
                        Order(it.book, it.price, it.amount)
                    }
                    val asks: List<Order> = response.body()?.payload?.asks!!.map {
                        Order(it.book, it.price, it.amount)
                    }
                    _orderBook.value = NetworkResponse.Success(OrderBook(asks, bids))
                }
            }
        )
    }

    sealed class NetworkResponse {
        data class Success<T>(val data: T) : NetworkResponse()
        data class Error(@StringRes val errorId: Int) : NetworkResponse()
    }
}