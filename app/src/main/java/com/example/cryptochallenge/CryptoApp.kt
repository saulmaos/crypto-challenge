package com.example.cryptochallenge

import android.app.Application
import com.example.cryptochallenge.data.local.CryptoDatabase
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.data.remote.Networking
import com.example.cryptochallenge.data.repository.BooksRepository
import com.example.cryptochallenge.data.repository.CoinDetailsRepository
import com.example.cryptochallenge.data.repository.dataSources.*
import com.example.cryptochallenge.utils.connectivity.ConnectivityLiveData

class CryptoApp : Application() {

    private val networkService: NetworkService by lazy {
        Networking.create(BuildConfig.BASE_URL)
    }

    private val booksRetrofitDataSource: RemoteBooksDataSource by lazy {
        BooksRetrofitDataSource(networkService)
    }

    private val tickerRetrofitDataSource: RemoteTickerDataSource by lazy {
        TickerRetrofitDataSource(networkService)
    }

    private val orderRetrofitDataSource: RemoteOrderDataSource by lazy {
        OrderRetrofitDataSource(networkService)
    }

    val coinDetailsRepository: CoinDetailsRepository by lazy {
        CoinDetailsRepository(tickerRetrofitDataSource, orderRetrofitDataSource)
    }

    private val database: CryptoDatabase by lazy {
        CryptoDatabase.getDatabase(applicationContext)
    }

    private val booksRoomDataSource: LocalBooksDataSource by lazy {
        BooksRoomDataSource(database.bookDao())
    }

    val booksRepository: BooksRepository by lazy {
        BooksRepository(booksRetrofitDataSource, booksRoomDataSource)
    }

    val connectivityLiveData: ConnectivityLiveData by lazy {
        ConnectivityLiveData(this)
    }
}