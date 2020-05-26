package com.ravinada.mvvmarchitecture.network

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ravinada.mvvmarchitecture.BuildConfig
import com.ravinada.mvvmarchitecture.utils.App
import com.ravinada.mvvmarchitecture.utils.AppConstant
import gomechanic.executiveutils.AppStorePreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkBuilder {

    fun <T> create(apiType: Class<T>,sharedPreferences: AppStorePreferences) =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(gmHttpClient(sharedPreferences))
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(apiType)

    private fun gmHttpClient(sharedPreferences : AppStorePreferences): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY

            })
        val auth = sharedPreferences.getString(App.applicationContext(), AppConstant.AUTHORIZATION)
        if (auth.isNotEmpty()) {
            builder.addInterceptor { chain ->
                var request = chain.request()
                val requestBuilder = request.newBuilder()
                    .header(AppConstant.AUTHORIZATION, String.format("%s %s", "Bearer", auth))
                Log.e("Bearer",auth)

                request = requestBuilder.build()
                chain.proceed(request)
            }
        }
        return builder.build()
    }

}
