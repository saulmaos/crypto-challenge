package com.example.cryptochallenge.ui.detailActivity

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cryptochallenge.R
import com.example.cryptochallenge.data.model.OrderBook
import com.example.cryptochallenge.data.model.Ticker
import com.example.cryptochallenge.data.repository.CoinDetailsRepository
import com.example.cryptochallenge.utils.Event
import com.example.cryptochallenge.utils.connectivity.NetworkHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class DetailViewModel(
    private val coinDetailsRepository: CoinDetailsRepository,
    private val networkHelper: NetworkHelper,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {
    private var tickerRequestFinished = false
    private var orderBookRequestFinished = false

    private val _events = MutableLiveData<Event<DetailNavigation>>()
    val events: LiveData<Event<DetailNavigation>> = _events

    private val _pair = MutableLiveData<Pair<String, String>>()
    val pair: LiveData<Pair<String, String>> = _pair

    fun onInitialRequest(book: String?) {
        networkHelper.observable()
            .subscribe(
                { requestData(it, book) },
                { it.printStackTrace() }
            ).let { compositeDisposable.add(it) }

        book?.let {
            val params = it.split("_")
            _pair.value = Pair(params[0], params[1])
            if (!networkHelper.isNetworkConnected()) {
                _events.value = Event(DetailNavigation.ShowLoading)
                requestLocalTicker(it)
                requestLocalOrderBook(it)
            }
        }
    }

    fun onReloadPressed(book: String?) {
        requestData(networkHelper.isNetworkConnected(), book)
    }

    private fun requestData(isConnected: Boolean, book: String?) {
        tickerRequestFinished = false
        orderBookRequestFinished = false
        if (isConnected) {
            book?.let {
                _events.value = Event(DetailNavigation.ShowLoading)
                requestRemoteTicker(it)
                requestRemoteOrderBook(it)
            }
        } else {
            _events.value = Event(DetailNavigation.Error(R.string.error_no_internet))
        }
    }

    private fun requestRemoteOrderBook(book: String) {
        coinDetailsRepository.requestOrderBook(book)
            .flatMap { orderBook ->
                coinDetailsRepository.deleteOrderBook(book).map { orderBook }
            }
            .flatMap { orderBook ->
                coinDetailsRepository.saveOrderBook(orderBook)
                    .map { orderBook }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _events.value = Event(DetailNavigation.OrderBookResult(it))
                    orderBookRequestFinished = true
                    if (tickerRequestFinished && orderBookRequestFinished)
                        _events.value = Event(DetailNavigation.HideLoading)
                },
                {
                    it.printStackTrace()
                    requestLocalOrderBook(book)
                }
            ).let { compositeDisposable.add(it) }
    }

    private fun requestRemoteTicker(book: String) {
        coinDetailsRepository.requestTicker(book)
            .flatMap { ticker ->
                coinDetailsRepository.saveTicker(ticker).map { ticker }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _events.value = Event(DetailNavigation.TickerResult(it))
                    tickerRequestFinished = true
                    if (tickerRequestFinished && orderBookRequestFinished)
                        _events.value = Event(DetailNavigation.HideLoading)
                },
                {
                    it.printStackTrace()
                    requestLocalTicker(book)
                }
            ).let { compositeDisposable.add(it) }
    }

    private fun requestLocalOrderBook(book: String) {
        coinDetailsRepository.getLocalOrderBook(book)
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                orderBookRequestFinished = true
                if (tickerRequestFinished && orderBookRequestFinished)
                    _events.value = Event(DetailNavigation.HideLoading)
            }
            .subscribe(
                {
                    if (it.asks.isEmpty()) _events.value =
                        Event(DetailNavigation.Error(R.string.error_on_request_data))
                    else _events.value = Event(DetailNavigation.OrderBookResult(it))
                },
                {
                    _events.value =
                        Event(DetailNavigation.Error(R.string.error_on_request_data))
                }
            ).let { compositeDisposable.add(it) }
    }

    private fun requestLocalTicker(book: String) {
        coinDetailsRepository.getLocalTicker(book)
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                tickerRequestFinished = true
                if (tickerRequestFinished && orderBookRequestFinished)
                    _events.value = Event(DetailNavigation.HideLoading)
            }
            .subscribe(
                {
                    _events.value = Event(DetailNavigation.TickerResult(it))
                },
                {
                    it.printStackTrace()
                    _events.value = Event(
                        DetailNavigation.TickerResult(Ticker.defaultTicker())
                    )
                    _events.value =
                        Event(DetailNavigation.Error(R.string.error_on_request_data))
                }
            ).let { compositeDisposable.add(it) }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    sealed class DetailNavigation {
        data class Error(@StringRes val errorId: Int) : DetailNavigation()
        data class TickerResult(val ticker: Ticker) : DetailNavigation()
        data class OrderBookResult(val orderBook: OrderBook) : DetailNavigation()
        object HideLoading : DetailNavigation()
        object ShowLoading : DetailNavigation()
    }
}