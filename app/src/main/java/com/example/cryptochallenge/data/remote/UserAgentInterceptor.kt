package com.example.cryptochallenge.data.remote

import android.os.Build
import com.example.cryptochallenge.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor: Interceptor {
    companion object {
        private const val USER_AGENT = "User-Agent"
    }

    private val userAgent: String =
        "Crypto-Challenge/" +
                "${BuildConfig.VERSION_NAME} " +
                "(${BuildConfig.APPLICATION_ID}; " +
                "build:${BuildConfig.VERSION_CODE} " +
                "Android SDK ${Build.VERSION.SDK_INT})"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestWithUserAgent = request.newBuilder()
            .header(USER_AGENT, userAgent)
            .build()

        return chain.proceed(requestWithUserAgent)
    }
}