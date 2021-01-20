package com.example.cryptochallenge.data.repository

import androidx.annotation.StringRes

sealed class NetworkResponse<out T> {
    data class Success<T>(val data: T) : NetworkResponse<T>()
    data class Error(@StringRes val errorId: Int) : NetworkResponse<Nothing>()
}