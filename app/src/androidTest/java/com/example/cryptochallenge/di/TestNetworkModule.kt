package com.example.cryptochallenge.di

import com.example.cryptochallenge.data.remote.FakeNetworkService
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.utils.connectivity.FakeNetworkHelper
import com.example.cryptochallenge.utils.connectivity.NetworkHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
abstract class TestNetworkModule {

    @Singleton
    @Binds
    abstract fun bindNetworkHelper(networkHelperImpl: FakeNetworkHelper): NetworkHelper
}

@InstallIn(ApplicationComponent::class)
@Module
object TestNetworkServiceModule {

    @Singleton
    @Provides
    fun provideNetworkService(): NetworkService =
        FakeNetworkService()
}

//@InstallIn(ApplicationComponent::class)
//@Module
//object ConnectivityModule {
//
//    @Provides
//    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
//        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//}