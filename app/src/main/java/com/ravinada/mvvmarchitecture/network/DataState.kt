package com.ravinada.mvvmarchitecture.network

sealed class DataState<T> {

    data class Success<T>(val data: T) : DataState<T>()
    data class Failure<T>(val errorCode: Int?, val errorMessage: String, val detailedError: Map<String, String>) :
        DataState<T>()
}