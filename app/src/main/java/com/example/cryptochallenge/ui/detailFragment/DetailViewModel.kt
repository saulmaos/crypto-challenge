package com.example.cryptochallenge.ui.detailFragment

import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.repository.CoinDetailsRepository
import com.example.cryptochallenge.utils.Event
import com.example.cryptochallenge.utils.PriceChange
import com.example.cryptochallenge.utils.connectivity.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch

class DetailViewModel @ViewModelInject constructor(
    private val coinDetailsRepository: CoinDetailsRepository,
    private val networkHelper: NetworkHelper,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {
    private var tickerRequestFinished = false
    private var orderBookRequestFinished = false

    private val _pair = MutableLiveData<Pair<String, String>>()
    val pair: LiveData<Pair<String, String>> = _pair

    private val _ticker = MutableLiveData<Ticker>()
    val ticker: LiveData<Ticker> = _ticker

    private val _orderBook = MutableLiveData<OrderBook>()
    val orderBook: LiveData<OrderBook> = _orderBook

    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> = _isLoading

    private val _error = MutableLiveData<Event<Int>>()
    val error: LiveData<Event<Int>> = _error

    /*
    * onInitialRequest() will be called every time onViewCreated() (from mainFragment).
    * `if (events.value != null)` is used to avoid calling it when config changes occur
    * It's necessary to check manually for the internet connection status as explained
    * in NetworkHelperImpl
    */
    fun onInitialRequest(book: String) {
        if (pair.value != null) return
        networkHelper.observable()
            .subscribe(
                { requestData(it, book) },
                { it.printStackTrace() }
            ).let { compositeDisposable.add(it) }

        book.let {
            val params = it.split("_")
            _pair.value = Pair(params[0], params[1])
            if (!networkHelper.isNetworkConnected()) {
                _isLoading.value = Event(true)
                requestLocalTicker(it)
                requestLocalOrderBook(it)
            }
        }
    }

    fun onReloadPressed(book: String) {
        requestData(networkHelper.isNetworkConnected(), book)
    }

    private fun requestData(isConnected: Boolean, book: String) {
        tickerRequestFinished = false
        orderBookRequestFinished = false
        if (isConnected) {
            book.let {
                _isLoading.value = Event(true)
                requestRemoteTicker(it)
                requestRemoteOrderBook(it)
            }
        } else {
            _error.value = Event(R.string.error_no_internet)
        }
    }

    /*
    * Last previous orderBook is deleted before the new one is saved. This is to ensure that
    * only the latest orders are saved
    */
    private fun requestRemoteOrderBook(book: String) = viewModelScope.launch {
        try {
            val orderBook = coinDetailsRepository.requestOrderBook(book)
            coinDetailsRepository.deleteOrderBook(book)
            coinDetailsRepository.saveOrderBook(orderBook)

            _orderBook.value = orderBook
            orderBookRequestFinished = true
            if (tickerRequestFinished && orderBookRequestFinished)
                _isLoading.value = Event(false)
        } catch (e: Exception) {
            e.printStackTrace()
            requestLocalOrderBook(book)
        }
    }

    private fun requestRemoteTicker(book: String) = viewModelScope.launch {
        try {
            val ticker: Ticker = coinDetailsRepository.requestTicker(book)
            ticker.lastPriceChangedRegardingThePreviousOne = calculateIfThereIsAPriceChange(ticker)
            coinDetailsRepository.saveTicker(ticker)
            _ticker.value = ticker
            tickerRequestFinished = true
            if (tickerRequestFinished && orderBookRequestFinished)
                _isLoading.value = Event(false)
        } catch (e: Exception) {
            e.printStackTrace()
            requestLocalTicker(book)
        }
    }

    private fun calculateIfThereIsAPriceChange(currentTicker: Ticker): PriceChange {
        return _ticker.value?.let {
            val oldLastPrice = it.last.toDouble()
            val currentLastPrice = currentTicker.last.toDouble()

            when {
                currentLastPrice > oldLastPrice -> PriceChange.CURRENT_IS_HIGHER
                currentLastPrice < oldLastPrice -> PriceChange.CURRENT_IS_LOWER
                else -> PriceChange.NO_CHANGE
            }
        } ?: PriceChange.NO_CHANGE
    }

    private fun requestLocalOrderBook(book: String) {
        coinDetailsRepository.getLocalOrderBook(book)
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                orderBookRequestFinished = true
                if (tickerRequestFinished && orderBookRequestFinished)
                    _isLoading.value = Event(false)
            }
            .subscribe(
                {
                    if (it.asks.isEmpty()) _error.value = Event(R.string.error_on_request_data)
                    else _orderBook.value = it
                },
                {
                    _error.value = Event(R.string.error_on_request_data)
                }
            ).let { compositeDisposable.add(it) }
    }

    private fun requestLocalTicker(book: String) {
        coinDetailsRepository.getLocalTicker(book)
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                tickerRequestFinished = true
                if (tickerRequestFinished && orderBookRequestFinished)
                    _isLoading.value = Event(false)
            }
            .subscribe(
                {
                    _ticker.value = it
                },
                {
                    it.printStackTrace()
                    _ticker.value = Ticker.defaultTicker()
                    _error.value = Event(R.string.error_on_request_data)
                }
            ).let { compositeDisposable.add(it) }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    sealed class DetailNavigation {
        data class Error(@StringRes val errorId: Int) : DetailNavigation()
        object HideLoading : DetailNavigation()
        object ShowLoading : DetailNavigation()
    }
}