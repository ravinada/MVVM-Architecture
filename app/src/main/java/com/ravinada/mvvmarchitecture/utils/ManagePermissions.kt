package com.ravinada.mvvmarchitecture.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ravinada.mvvmarchitecture.utils.CommonUtils.showToast

class ManagePermissions(private val context: Context, private val list: List<String>, private val code: Int) {
    // Check permissions at runtime
    fun checkPermissions(): Boolean {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
            return false
        }
        return true

    }

    // Check permissions status
    private fun isPermissionsGranted(): Int {
        // PERMISSION_GRANTED : Constant Value: 0
        // PERMISSION_DENIED : Constant Value: -1
        var counter = 0
        for (permission in list) {
            counter += ContextCompat.checkSelfPermission(context, permission)
        }
        return counter
    }

    // Find the first denied permission
    private fun deniedPermission(): String {
        for (permission in list) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) return permission
        }
        return ""
    }

    // Request the permissions at run time
    private fun requestPermissions() {
        val permission = deniedPermission()
        if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)) {
            // Show an explanation asynchronously
            showToast(context, "Should show an explanation.")
            ActivityCompat.requestPermissions(context, list.toTypedArray(), code)
        } else {
            ActivityCompat.requestPermissions(context, list.toTypedArray(), code)
        }
    }

    // Process permissions result
    fun processPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean {
        var result = 0
        if (grantResults.isNotEmpty()) {
            for (item in grantResults) {
                result += item
            }
        }
        if (result == PackageManager.PERMISSION_GRANTED) return true
        return false
    }
}