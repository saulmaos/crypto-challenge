package com.example.cryptochallenge.utils.connectivity

import com.example.cryptochallenge.utils.connectivity.FakeNetworkConfig.emitValue
import com.example.cryptochallenge.utils.connectivity.FakeNetworkConfig.isThereInternet
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FakeNetworkHelper : NetworkHelper {
    private val observable: Observable<Boolean>

    init {
        observable = Observable.create {
            if (emitValue)
                it.onNext(isThereInternet)
            val internetEmitter = object : InternetEmitter {
                override fun onChange(isInternetConnected: Boolean) {
                    it.onNext(isInternetConnected)
                }
            }
            FakeNetworkConfig.subscribe(internetEmitter)
        }
    }

    override fun observable(): Observable<Boolean> {
        return observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun isNetworkConnected(): Boolean {
        return isThereInternet
    }
}

object FakeNetworkConfig {
    var emitValue = true
    var isThereInternet = false
    private var internetEmitter: InternetEmitter? = null

    fun subscribe(internetEmitter: InternetEmitter) {
        this.internetEmitter = internetEmitter
    }

    fun emitInternetConnectionActive() {
        internetEmitter?.onChange(true)
    }

    fun emitInternetConnectionLost() {
        internetEmitter?.onChange(false)
    }
}

interface InternetEmitter {
    fun onChange(isInternetConnected: Boolean)
}