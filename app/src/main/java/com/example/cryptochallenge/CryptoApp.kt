package com.example.cryptochallenge

import android.app.Application
import com.example.cryptochallenge.data.local.CryptoDatabase
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.data.remote.Networking
import com.example.cryptochallenge.data.repository.BooksRepository
import com.example.cryptochallenge.data.repository.CoinDetailsRepository
import com.example.cryptochallenge.data.repository.dataSources.*
import com.example.cryptochallenge.utils.connectivity.NetworkHelper
import com.example.cryptochallenge.utils.connectivity.NetworkHelperImpl

class CryptoApp : Application() {

    private val networkService: NetworkService by lazy {
        Networking.create(BuildConfig.BASE_URL)
    }

    private fun getCryptoNetworkService(): NetworkService {
        return fakeNetworkService
            ?: networkService
    }

    fun getNetworkHelper(): NetworkHelper {
        return fakeNetworkHelper?.let { return it }
            ?: networkHelperImpl
    }

    private val networkHelperImpl: NetworkHelper by lazy {
        NetworkHelperImpl(this)
    }

    private fun getBooksRetrofitDataSource(): RemoteBooksDataSource {
        return BooksRetrofitDataSource(getCryptoNetworkService())
    }

    private fun getTickerRetrofitDataSource(): RemoteTickerDataSource {
        return TickerRetrofitDataSource(getCryptoNetworkService())
    }

    private fun getOrderBookRetrofitDataSource(): RemoteOrderBookDataSource {
        return OrderBookRetrofitDataSource(getCryptoNetworkService())
    }

    private fun getBooksRoomDataSource(): LocalBooksDataSource {
        return BooksRoomDataSource(getCryptoDatabase().bookDao())
    }

    private fun getTickerRoomDataSource(): LocalTickerDataSource {
        return TickerRoomDataSource(getCryptoDatabase().tickerDao())
    }

    private fun getOrderBookRoomDataSource(): LocalOrderBookDataSource {
        return OrderBookRoomDataSource(getCryptoDatabase().orderBookDao())
    }

    fun getCoinDetailsRepository(): CoinDetailsRepository {
        return CoinDetailsRepository(
            getTickerRetrofitDataSource(),
            getOrderBookRetrofitDataSource(),
            getTickerRoomDataSource(),
            getOrderBookRoomDataSource()
        )
    }

    private val database: CryptoDatabase by lazy {
        CryptoDatabase.getDatabase(applicationContext)
    }

    private fun getCryptoDatabase(): CryptoDatabase {
        return fakeDatabase?.let { return it }
            ?: database
    }

    fun getBooksRepository(): BooksRepository {
        return BooksRepository(getBooksRetrofitDataSource(), getBooksRoomDataSource())
    }

    var fakeNetworkHelper: NetworkHelper? = null

    var fakeDatabase: CryptoDatabase? = null

    var fakeNetworkService: NetworkService? = null
}