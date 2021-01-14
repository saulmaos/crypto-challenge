package com.example.cryptochallenge

import android.app.Application
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.data.remote.Networking
import com.example.cryptochallenge.data.repository.BitsoRepository
import com.example.cryptochallenge.utils.connectivity.ConnectivityLiveData

class CryptoApp: Application() {

    private val networkService: NetworkService by lazy {
        Networking.create(BuildConfig.BASE_URL)
    }

    val bitsoRepository: BitsoRepository by lazy {
        BitsoRepository(networkService)
    }

    val connectivityLiveData: ConnectivityLiveData by lazy {
        ConnectivityLiveData(this)
    }
}