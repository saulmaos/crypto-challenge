package com.example.cryptochallenge.di

import android.content.Context
import android.net.ConnectivityManager
import com.example.cryptochallenge.BuildConfig
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.data.remote.Networking
import com.example.cryptochallenge.utils.connectivity.NetworkHelper
import com.example.cryptochallenge.utils.connectivity.NetworkHelperImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
abstract class NetworkModule {

    @Singleton
    @Binds
    abstract fun bindNetworkHelper(networkHelperImpl: NetworkHelperImpl): NetworkHelper
}

@InstallIn(ApplicationComponent::class)
@Module
object NetworkServiceModule {

    @Singleton
    @Provides
    fun provideNetworkService(): NetworkService =
        Networking.create(BuildConfig.BASE_URL)
}

@InstallIn(ApplicationComponent::class)
@Module
object ConnectivityModule {

    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}