package com.ravinada.mvvmarchitecture.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.ravinada.mvvmarchitecture.BuildConfig
import com.ravinada.mvvmarchitecture.R
import com.ravinada.mvvmarchitecture.api.API
import com.ravinada.mvvmarchitecture.api.APIRepository
import com.ravinada.mvvmarchitecture.api.APIRepositoryImpl
import com.ravinada.mvvmarchitecture.base.BaseViewModel
import com.ravinada.mvvmarchitecture.network.NetworkBuilder
import gomechanic.executiveutils.AppStorePreferences
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Modules {
    private val applicationModules = module {
        single {
            get<Application>().run {
                AppStorePreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
            }
        }
        // Room module
        //        single {
        //            Room.databaseBuilder(get(), GoMechDB::class.java, BuildConfig.DEFAULT_ROOM).allowMainThreadQueries()
        //                .fallbackToDestructiveMigration().build()
        //        }
    }
        private val viewModelModules = module {
            viewModel { BaseViewModel(get(), get()) }
        }

//        private val daoModules = module {
//            single { RoomModule(get()).getRoomDao() }
//        }

        private val networkModules = module {
            single<API> { NetworkBuilder.create( API::class.java, get()) }
        }

        private val repoModules = module {
            single<APIRepository> { APIRepositoryImpl(get(), get()) }
            }

    fun getAll() = listOf(applicationModules, viewModelModules, networkModules,repoModules)
}