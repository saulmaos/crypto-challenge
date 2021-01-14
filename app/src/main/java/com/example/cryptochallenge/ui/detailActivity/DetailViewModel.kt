package com.example.cryptochallenge.ui.detailActivity

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.model.Order
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.repository.BitsoRepository
import com.example.cryptochallenge.utils.Event

class DetailViewModel(
    private val repository: BitsoRepository
) : ViewModel() {

    private var isConnected: Boolean = false
    private val _events = MutableLiveData<Event<DetailNavigation>>()
    val events: LiveData<Event<DetailNavigation>> = _events

    val ticker: LiveData<Event<TickerResponse>> = Transformations.map(repository.ticker) {
        _events.value = Event(DetailNavigation.HideLoading)
        if (!isConnected) {
            return@map Event(TickerResponse.TickerDefaultResult("N/A"))
        }
        when(it) {
            is BitsoRepository.NetworkResponse.Success<*> -> {
                Event(TickerResponse.TickerResult(it.data as Ticker))
            }
            is BitsoRepository.NetworkResponse.Error -> {
                Event(TickerResponse.Error(it.errorId))
            }
        }
    }

    private val _pair = MutableLiveData<String>()
    val pair: LiveData<String> = _pair

    val lastOrder: LiveData<Event<OrderResponse>> = Transformations.map(repository.orderBook) {
        _events.value = Event(DetailNavigation.HideLoading)
        if (!isConnected) {
            return@map Event(OrderResponse.OrderResult(
                Order("N/A", "N/A", "N/A")
            ))
        }
        when(it) {
            is BitsoRepository.NetworkResponse.Success<*> -> {
                val orderBook = it.data as OrderBook
                Event(OrderResponse.OrderResult(orderBook.asks[0]))
            }
            is BitsoRepository.NetworkResponse.Error -> {
                Event(OrderResponse.Error(it.errorId))
            }
        }
    }

    fun onInternetChange(isConnected: Boolean, book: String?) {
        this.isConnected = isConnected
        requestData(isConnected, book)
    }

    fun onReloadPressed(book: String?) {
        requestData(isConnected, book)
    }

    private fun requestData(isConnected: Boolean, book: String?) {
        if (isConnected) {
            book?.let {
                _pair.value = it.replace("_", "/")
                _events.value = Event(DetailNavigation.ShowLoading)
                repository.requestTickerByBook(book)
                repository.requestOrdersByBook(book)
            }
        } else {
            _events.value = Event(DetailNavigation.NoInternet(R.string.error_no_internet))
        }
    }

    sealed class DetailNavigation {
        data class NoInternet(@StringRes val errorId: Int) : DetailNavigation()
        object HideLoading : DetailNavigation()
        object ShowLoading : DetailNavigation()
    }

    sealed class OrderResponse {
        data class OrderResult(val order: Order): OrderResponse()
        data class Error(@StringRes val errorId: Int): OrderResponse()
    }

    sealed class TickerResponse {
        data class TickerResult(val ticker: Ticker): TickerResponse()
        data class TickerDefaultResult(val default: String): TickerResponse()
        data class Error(@StringRes val errorId: Int): TickerResponse()
    }
}