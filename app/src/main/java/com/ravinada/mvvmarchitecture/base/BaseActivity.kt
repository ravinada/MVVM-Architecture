package com.ravinada.mvvmarchitecture.base

import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ravinada.mvvmarchitecture.R
import com.ravinada.mvvmarchitecture.utils.CommonUtils

abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity(), View.OnClickListener {

    private var progressBar: ProgressBar? = null
    protected abstract fun getLayout(): Int
    abstract val viewModel: VM

    fun showLoader(shouldShow: Boolean) {
        progressBar?.let { pb ->
            pb.visibility = if (shouldShow) View.VISIBLE else View.GONE
        }
    }

    fun onNetworkChanged(connected: Boolean) {
        val fragment = CommonUtils.getTopVisibleFragment(supportFragmentManager, R.id.fragment_container)
        if (fragment is BaseFragment) {
            toRedirectBottomNav(fragment, connected)
        }
    }

    private fun toRedirectBottomNav(fragment: Fragment, connected: Boolean) {
        (fragment as BaseFragment).onNetworkConnectionChanged(connected)
    }
}