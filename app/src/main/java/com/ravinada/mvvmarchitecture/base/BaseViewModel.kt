package com.ravinada.mvvmarchitecture.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.ravinada.mvvmarchitecture.R
import com.ravinada.mvvmarchitecture.api.APIRepository
import com.ravinada.mvvmarchitecture.network.ConnectionLiveData
import com.ravinada.mvvmarchitecture.network.DataState
import com.ravinada.mvvmarchitecture.network.RequestException
import com.ravinada.mvvmarchitecture.network.error.errorCode
import gomechanic.executiveutils.AppStorePreferences
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import kotlin.coroutines.CoroutineContext

open class BaseViewModel(private val app: Application, private val apiRepo: APIRepository) : AndroidViewModel(app), CoroutineScope {

    private val failedJobIdList = mutableSetOf<String>()
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onCleared() {
        super.onCleared()
        connectionLiveData.removeObserver(connectionObserver)
        failedJobIdList.clear()
        job.cancel()
    }

    private fun addJobId(jobId: String) {
        failedJobIdList.add(jobId)
    }

    private fun removeJobId(jobId: String) {
        failedJobIdList.remove(jobId)
    }

    protected fun CoroutineScope.launch(context: CoroutineContext = kotlin.coroutines.EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        doOnComplete: (throwable: Throwable?) -> Unit = {},
        id: String,
        block: suspend CoroutineScope.() -> Unit): Job {
        return launch(context, start, block).apply {
            invokeOnCompletion {
                if (it != null) addJobId(id)
                else removeJobId(id)

                doOnComplete(it)
            }
        }
    }

    protected fun <T> CoroutineScope.async(context: CoroutineContext = kotlin.coroutines.EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        doOnComplete: (throwable: Throwable?) -> Unit = {},
        id: String, block: suspend CoroutineScope.() -> T): Deferred<T> {
        return async(context, start, block).apply {
            invokeOnCompletion {
                if (it != null) addJobId(id)
                else removeJobId(id)
                doOnComplete(it)
            }
        }
    }

    protected fun CoroutineScope.addToFailedJobStack(throwable: Throwable? = null) {
        coroutineContext.cancel()
    }

    private fun onConnectedToNetwork() {
        resumeFailedJob(failedJobIdList)
    }

    open fun resumeFailedJob(jobIdList: Iterable<String>) {}

    private val connectionLiveData = ConnectionLiveData(WeakReference(app))
    private val connectionObserver = Observer<Boolean> { isConnected ->
        if (isConnected) onConnectedToNetwork()
    }

    init {
        connectionLiveData.observeForever(connectionObserver)
    }

    protected fun <T> BaseResult.Failure<*>.toFailureDataState() = when (exception) {
        is UnknownHostException, is SocketException, is SocketTimeoutException, is TimeoutException, is ConnectException ->
            DataState.Failure(null, app.getString(R.string.no_connection).orEmpty(), kotlin.collections.emptyMap())
        is RequestException -> DataState.Failure(exception.errorCode, if (exception.localizedMessage.isEmpty()) app.getString(R.string.default_error_network) else exception.localizedMessage, exception.errorMap)
        else -> DataState.Failure<T>(exception.errorCode, app.getString(R.string.default_error_network), kotlin.collections.emptyMap())
    }

    private fun getSharedPreference(): AppStorePreferences {
        return apiRepo.getSharedPreferences()
    }

    fun putStringSharedPreference(key: String, value: String) {
        getSharedPreference().putString(app.applicationContext, key, value)
    }

    fun getStringSharedPreference(key: String): String {
        return getSharedPreference().getString(app.applicationContext, key)
    }

    fun getIntSharedPreference(key: String): Int {
        return getSharedPreference().getInt(app.applicationContext, key)
    }

    fun putIntSharedPreference(key: String, value: Int) {
        getSharedPreference().putInt(app.applicationContext, key, value)
    }

    fun putBooleanSharedPreference(key: String, value: Boolean) {
        getSharedPreference().putBoolean(app.applicationContext, key, value)
    }

    fun getBooleanSharedPreference(key: String): Boolean {
        return getSharedPreference().getBoolean(app.applicationContext, key)
    }

    fun clearData() {
        getSharedPreference().clearAllSharedPrefrences(app.applicationContext)
    }
}