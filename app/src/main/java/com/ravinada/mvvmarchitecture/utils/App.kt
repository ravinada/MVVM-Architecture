package com.ravinada.mvvmarchitecture.utils

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.ravinada.mvvmarchitecture.R
import com.ravinada.mvvmarchitecture.di.Modules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import timber.log.Timber

class App : MultiDexApplication() {

    init {
        instance = this
    }

    companion object {
        private var instance: App? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startModule()
        try {
            val remoteConfig = Firebase.remoteConfig
            //Set default cache duration to 3 hours
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = (1200)
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        } catch (e: Exception) {
            //Nothing we can dp
        }
    }

    private fun startModule() {
        startKoin {
            androidContext(this@App)
            modules(Modules.getAll())
        }
    }

    fun reStartModule() {
        stopKoin()
        startKoin {
            androidContext(this@App)
            modules(Modules.getAll())
        }
    }

    fun getContext(): Context {
        return applicationContext
    }
}