package com.example.cryptochallenge.di

import android.content.Context
import android.net.ConnectivityManager
import com.example.cryptochallenge.BuildConfig
import com.example.cryptochallenge.data.remote.NETWORK_CALL_TIMEOUT
import com.example.cryptochallenge.data.remote.NetworkService
import com.example.cryptochallenge.data.remote.UserAgentInterceptor
import com.example.cryptochallenge.utils.connectivity.NetworkHelper
import com.example.cryptochallenge.utils.connectivity.NetworkHelperImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
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
    fun provideNetworkService(retrofit: Retrofit): NetworkService =
        retrofit.create(NetworkService::class.java)

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
                    }
            )
            .addInterceptor(UserAgentInterceptor())
            .readTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
}

@InstallIn(ApplicationComponent::class)
@Module
object ConnectivityModule {

    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}