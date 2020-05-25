package com.ravinada.mvvmarchitecture.base

sealed class BaseResult<T> {

    data class Success<T>(val body: T, val code: Int) : BaseResult<T>()
    data class Failure<T>(val exception: Throwable) : BaseResult<T>()
}