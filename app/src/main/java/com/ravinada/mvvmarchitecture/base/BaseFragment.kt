package com.ravinada.mvvmarchitecture.base

import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import com.ravinada.mvvmarchitecture.utils.CommonUtils

abstract class BaseFragment : AppCompatDialogFragment(), View.OnClickListener {

    fun onNetworkConnectionChanged(connected: Boolean) {
        try {
            val childManager = childFragmentManager
            val fragments: List<Fragment>? = CommonUtils.getListFragment(childManager)
            fragments?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    it.stream().filter { fragment -> fragment is BaseFragment }
                        .forEach { fragment ->
                            (fragment as BaseFragment).onNetworkConnectionChanged(connected)
                        }
                } else {
                    for (i in it.indices) {
                        val baseFragment = it[i] as BaseFragment
                        baseFragment.onNetworkConnectionChanged(connected)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}