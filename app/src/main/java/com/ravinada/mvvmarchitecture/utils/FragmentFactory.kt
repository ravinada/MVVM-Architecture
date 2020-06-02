package com.ravinada.mvvmarchitecture.utils

import android.net.Uri
import android.os.Bundle
import com.ravinada.mvvmarchitecture.base.BaseFragment

class FragmentFactory private constructor() {
    init {
        throw IllegalStateException("FragmentFactory class")
    }

    abstract class Screens private constructor() {
        init {
            throw IllegalStateException("Screens class")
        }

        companion object {
            const val HOME_FRAGMENT = "HOME_FRAGMENT"
        }
    }

    companion object {

        /**
         * Returns fragment either from not or base app
         */
        fun fragment(fragmentName: String, params: Bundle?): BaseFragment? {

            if (fragmentName.isNotEmpty()) {
                var baseFragment : BaseFragment? = null
                if (fragmentName.contains("http://") || fragmentName.contains("https://")) {

                    val uri = Uri.parse(fragmentName)
                    val pathValue = uri.pathSegments
                    val screenName = ""

                    if (pathValue != null && pathValue.isNotEmpty()) {
                        baseFragment = getFragmentFromAppPackage(screenName)?.apply {
                            arguments = params ?: Bundle()
                        }
                    }
                } else {
                    baseFragment = getFragmentFromAppPackage(fragmentName)?.apply {
                        arguments = params?:Bundle()
                    }
                }
                return baseFragment
            } else {
                return null
            }

        }

        /**
         * Returns fragment either from not or base app
         */
//        fun fragmentDialog(fragmentName: String, params: Bundle?): BaseDialogFragment? {
//
//            if (fragmentName.isNotEmpty()) {
//                var baseFragment : BaseDialogFragment? = null
//                if (fragmentName.contains("http://") || fragmentName.contains("https://")) {
//
//                    val uri = Uri.parse(fragmentName)
//                    val pathValue = uri.pathSegments
//                    val screenName = ""
//
//                    if (pathValue != null && pathValue.isNotEmpty()) {
//                        baseFragment = getDialogFragmentFromAppPackage(screenName)?.apply {
//                            arguments = params ?: Bundle()
//                        }
//                    }
//                } else {
//                    baseFragment = getDialogFragmentFromAppPackage(fragmentName)?.apply {
//                        arguments = params?:Bundle()
//                    }
//                }
//                return baseFragment
//            } else {
//                return null
//            }
//
//        }

        /**
         * Returns fragment from base application
         */
        private fun getFragmentFromAppPackage(fragment: String): BaseFragment? {

            return when (fragment) {
               // Screens.HOME_FRAGMENT -> HomeFragment()

                else -> {
                    null
                }
            }
        }


        /**
         * Returns fragment from base application
//         */
//        private fun getDialogFragmentFromAppPackage(fragment: String): BaseDialogFragment? {
//
//            return when (fragment) {
//               // Screens.REGISTRATION_NUMBER_FILTER->RegistrationNumberFilter()
//                else -> {
//                    null
//                }
//            }
//        }
//        fun fragmentBottomSheet(fragmentName: String, params: Bundle?): BaseBottomSheetFragment? {
//            return if (fragmentName.isNotEmpty()) {
//                getFragmentFromAppPackagefragmentBottomSheet(fragmentName)?.apply {
//                    arguments = params?:Bundle()
//                }
//            } else {
//                null
//            }
//        }

        /**
         * Returns fragment from base application
         */
//        private fun getFragmentFromAppPackagefragmentBottomSheet(fragment: String): BaseBottomSheetFragment? {
//
//            return when (fragment) {
//
//                Screens.ORDER_MENU_FRAGMENT -> OrderMenuBottomSheetFragment()
//                Screens.ORDER_ADD_PAYMENT_FRAGMENT->OrderAddPaymentFragment()
//                Screens.ORDER_ESTIMATE_DETAIL_FRAGMENT-> OrderEstimateDetailFragment()
//                Screens.SETTLEMENT_BOTTOM_FRAGMENT -> SettlementsBottomSheetFragment()
//                Screens.SEARCH_ORDER_BOTTOM_FRAGMENT -> OrderSearchBottomSheetFragment()
//                else -> {
//                    null
//                }
//
//
//            }
//        }
    }

}