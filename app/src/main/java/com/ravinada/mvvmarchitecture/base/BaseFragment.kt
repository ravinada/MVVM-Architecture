package com.ravinada.mvvmarchitecture.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.ravinada.mvvmarchitecture.R
import com.ravinada.mvvmarchitecture.images.ImagePicker
import com.ravinada.mvvmarchitecture.utils.CommonUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseFragment : AppCompatDialogFragment(), View.OnClickListener {
    open var fragmentViewHolder: BaseFragmentViewHolder? = null
    abstract val fragmentLayout: Int
    private var CAMERA = 11
    private var GALLERY = 12
    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.let { dialog ->
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onClick(v: View) {
        CommonUtils.hideKeypad(activity, v)
        //TODO when you add back button uncomment it
//        if (v.id == R.id.icBack) {
//            popBackStack()
//        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            return inflater.inflate(fragmentLayout, container, false)
        } catch (e: Exception) {
            e.message?.let { CommonUtils.showToast(activity, it) }
            throw e
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CommonUtils.hideKeyboard(activity as FragmentActivity)
        this.fragmentViewHolder = createFragmentViewHolder(view)
        onFragmentViewHolderCreated(this.fragmentViewHolder, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        onNetworkConnectionChanged(CommonUtils.isConnectionAvailable(activity))
    }

    override fun onPause() {
        EventBus.getDefault().unregister(this)
        super.onPause()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventBus) {
    }

    open fun createFragmentViewHolder(view: View): BaseFragmentViewHolder {
        return BaseFragmentViewHolder(view)
    }

    open fun onFragmentViewHolderCreated(
        viewHolder: BaseFragmentViewHolder?,
        savedInstanceState: Bundle?
    ) {
        //Log.e("Fragment Name", javaClass.toString())
    }

    open fun handleBackPress(): Boolean {
        return false
    }

    @SuppressLint("UseRequireInsteadOfGet")
    fun popBackStack() {
        fragmentManager?.let {
            try {
                CommonUtils.hideKeyboard(activity)
                fragmentManager!!.popBackStack()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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



    protected fun showLoader() {
        activity?.let {
            (it as BaseActivity<*>).showLoader(true)
        }
    }

    open inner class BaseFragmentViewHolder(rootView: View) {
        val progressBar: ProgressBar? = rootView.findViewById(R.id.materialLoader)
    }

    protected fun hideLoader() {
        activity?.let {
            (it as BaseActivity<*>).showLoader(false)
        }
    }

    override fun onDestroyView() {
        this.fragmentViewHolder = null
        super.onDestroyView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: BaseEventBusModel) {
    }

    companion object {

        fun addFragmentUtil(
            activity: FragmentActivity?,
            fragment: BaseFragment?,
            addToBackStack: Boolean = true,
            target: Int = R.id.fragment_container
        ) {
            CommonUtils.hideKeyboard(activity)
            activity?.let { activityIt ->
                fragment?.let { fragmentIt ->
                    addFragment(
                        activityIt.supportFragmentManager,
                        fragmentIt,
                        addToBackStack,
                        target
                    )
                }
            }

        }


        fun replaceFragmentUtil(
            activity: FragmentActivity?,
            fragment: BaseFragment?,
            addToBackStack: Boolean = true,
            target: Int = R.id.fragment_container
        ) {
            CommonUtils.hideKeyboard(activity)
            activity?.let { activityIt ->
                fragment?.let { fragmentIt ->
                    replaceFragment(
                        activityIt.supportFragmentManager,
                        fragmentIt,
                        addToBackStack,
                        target
                    )
                }
            }

        }

        @JvmOverloads
        fun addFragment(
            manager: FragmentManager?,
            fragment: BaseFragment?,
            addToBackStack: Boolean,
            target: Int = R.id.fragment_container
        ) {
            manager?.let { fragmentManager ->
                fragment?.let { passedFragment ->
                    add(fragmentManager, target, passedFragment, -1, -1, -1, -1, addToBackStack)
                }
            }

        }

        @JvmOverloads
        fun replaceFragment(
            manager: FragmentManager?,
            fragment: BaseFragment?,
            addToBackStack: Boolean,
            target: Int = R.id.fragment_container
        ) {
            manager?.let { fragmentManager ->
                fragment?.let { passedFragment ->
                    replace(fragmentManager, target, passedFragment, -1, -1, -1, -1, addToBackStack)
                }
            }

        }

        private fun add(
            fragmentManager: FragmentManager,
            targetId: Int,
            fragment: BaseFragment?,
            enter: Int,
            exit: Int,
            popEnter: Int,
            popExit: Int,
            addToBackStack: Boolean
        ) {
            fragment?.let {
                val transaction = fragmentManager.beginTransaction()
                transaction.add(targetId, fragment, fragment.javaClass.name)
                if (addToBackStack) {
                    transaction.addToBackStack(null)
                }
                transaction.commitAllowingStateLoss()
            }
        }

        private fun replace(
            fragmentManager: FragmentManager,
            targetId: Int,
            fragment: BaseFragment?,
            enter: Int,
            exit: Int,
            popEnter: Int,
            popExit: Int,
            addToBackStack: Boolean
        ) {
            fragment?.let {
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(targetId, fragment, fragment.javaClass.name)
                if (addToBackStack) {
                    transaction.addToBackStack(null)
                }
                transaction.commitAllowingStateLoss()
            }
        }


    }




    fun openImageGallery(fragment: Fragment, isMultiple: Boolean) {
        ImagePicker.Builder(fragment).mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
            .directory(ImagePicker.Directory.DEFAULT).extension(ImagePicker.Extension.PNG)
            .allowMultipleImages(isMultiple).enableDebuggingMode(true).build()
    }

}


