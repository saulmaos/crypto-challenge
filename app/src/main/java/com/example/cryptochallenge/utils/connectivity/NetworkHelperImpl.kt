package com.example.cryptochallenge.utils.connectivity

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/*
* Used to be notified when there is a change on the internet connection
*/
class NetworkHelperImpl @Inject constructor(
    private val connectivityManager: ConnectivityManager
) : NetworkHelper {
    /*
    * Used to emit events about the internet connection using the NetworkCallback but
    * this callback does not work when there's no internet and the user opens the app
    */
    private val observable: Observable<Boolean>

    init {
        observable = Observable.create {
            val builder = NetworkRequest.Builder()
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    it.onNext(true)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    it.onNext(false)
                }
            }
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
        }
    }

    override fun observable(): Observable<Boolean> {
        return observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /*
    * Used to check manually if there is a network available
    */
    override fun isNetworkConnected(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                // for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                // for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }
}