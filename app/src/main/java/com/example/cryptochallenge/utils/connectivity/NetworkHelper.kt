package com.example.cryptochallenge.utils.connectivity

import io.reactivex.Observable

interface NetworkHelper {

    fun observable(): Observable<Boolean>

    fun isNetworkConnected(): Boolean
}