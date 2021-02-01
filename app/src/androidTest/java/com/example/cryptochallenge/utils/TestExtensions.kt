package com.example.cryptochallenge.utils

import com.example.cryptochallenge.data.model.Book
import com.example.cryptochallenge.data.remote.response.PayloadAvailableBookResponse

fun List<Book>.toPayloadAvailableBookResponseList(): List<PayloadAvailableBookResponse> =
    map {
        PayloadAvailableBookResponse(it.book, it.minimumAmount, it.maximumAmount, it.minimumPrice, it.maximumPrice, it.minimumValue, it.maximumValue)
    }