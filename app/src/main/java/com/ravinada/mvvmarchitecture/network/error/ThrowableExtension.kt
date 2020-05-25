package com.ravinada.mvvmarchitecture.network.error

import com.ravinada.mvvmarchitecture.network.RequestException

fun Throwable.throwException(): Nothing = throw this

val Throwable.errorCode
    get() = when (this) {
        is RequestException -> statusCode
        else -> null
    }