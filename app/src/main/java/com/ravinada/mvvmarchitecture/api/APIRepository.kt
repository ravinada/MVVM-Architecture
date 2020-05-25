package com.ravinada.mvvmarchitecture.api

import gomechanic.executiveutils.AppStorePreferences

interface APIRepository {

    fun getSharedPreferences(): AppStorePreferences

}