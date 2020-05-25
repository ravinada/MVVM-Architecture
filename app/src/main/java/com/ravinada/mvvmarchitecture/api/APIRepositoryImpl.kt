package com.ravinada.mvvmarchitecture.api

import gomechanic.executiveutils.AppStorePreferences

class APIRepositoryImpl(private val api: API, private val sharePref: AppStorePreferences) : APIRepository {

    override fun getSharedPreferences(): AppStorePreferences {
        return sharePref
    }
}