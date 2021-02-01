package com.example.cryptochallenge.utils.connectivity

import io.reactivex.Observable

interface Network {

    fun observable(): Observable<>
}