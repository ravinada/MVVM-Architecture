package com.ravinada.mvvmarchitecture.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.ravinada.mvvmarchitecture.R
import com.ravinada.mvvmarchitecture.utils.CommonUtils

class NetworkErrorView : LinearLayout, View.OnClickListener {
    private var networkMessageView: TextView? = null
    private var tryAgainButton: Button? = null

    private var onClickListener: View.OnClickListener? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }
//
//    constructor(context: Context, attrs: AttributeSet,
//                defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        initView()
//    }

    private fun initView() {
        LayoutInflater.from(this.context)
                .inflate(R.layout.material_network_error_layout, this, true)
        this.networkMessageView = findViewById(R.id.networkMessageView)
        this.tryAgainButton = findViewById(R.id.tryAgainButton)
        this.tryAgainButton!!.setOnClickListener(this)
    }

    override fun setOnClickListener(onClickListener: View.OnClickListener?) {
        this.onClickListener = onClickListener
    }

    override fun onClick(view: View) {
        if (view.context is BaseActivity<*>) {
            val connection = CommonUtils
                    .isConnectionAvailable(view.context)
            (view.context as BaseActivity<*>)
                    .onNetworkChanged(connection)
        }
        if (onClickListener != null) {
            //            view.setId(R.id.networkErrorPageID);
            onClickListener!!.onClick(view)
        }

    }

    fun updateErrorView(errorType: Int) {
        val networkMessage: String?

        when (errorType) {
            ERROR_SERVER_INTERNAL -> {
                networkMessage = "Can't Connect\nServer Error"
                tryAgainButton!!.visibility = View.VISIBLE
            }
            ERROR_HEAVY_TRAFFIC -> {
                networkMessage = "Can't Connect\nPOKE"
                tryAgainButton!!.visibility = View.VISIBLE
            }
            ERROR_EXCEPTIONDTO -> {
                networkMessage = "Something is not right here.\nWe are working quickly to resolve the issue."
                tryAgainButton!!.visibility = View.VISIBLE
            }
            ERROR_TIMED_MAINTAINANCE_TIER_TWO, ERROR_TIMED_MAINTAINANCE_ALL, ERROR_TIMED_MAINTAINANCE -> {
                networkMessage = "Sorry, We are down for Maintenance!\nWe will be back soon. Regret for the inconvenience caused."
                tryAgainButton!!.visibility = View.INVISIBLE
            }
            ERROR_TIMEDOUT -> {
                tryAgainButton!!.visibility = View.VISIBLE
                networkMessage = "Sorry! \nThis page is taking longer than expected to load"
            }
            ERROR_CONNECTIVITY -> {
                networkMessage = resources.getString(R.string.no_internet_connection)
                tryAgainButton!!.visibility = View.VISIBLE
            }
            else -> {
                networkMessage = resources.getString(R.string.no_internet_connection)
                tryAgainButton!!.visibility = View.VISIBLE
            }
        }
        this.networkMessageView!!.text = networkMessage

    }

    companion object {

        val ERROR_CONNECTIVITY = 0
        val ERROR_SERVER_INTERNAL = 1
        val ERROR_HEAVY_TRAFFIC = 2
        val ERROR_TIMEDOUT = 3
        val ERROR_EXCEPTIONDTO = 5
        val ERROR_TIMED_MAINTAINANCE_ALL = 2001
        val ERROR_TIMED_MAINTAINANCE = 2004
        val ERROR_TIMED_MAINTAINANCE_TIER_TWO = 3002
    }

}
