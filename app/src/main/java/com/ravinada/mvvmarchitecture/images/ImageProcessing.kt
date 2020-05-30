package com.ravinada.mvvmarchitecture.images

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

internal object ImageProcessing {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun processMultiImage(context: Context, data: Intent): List<String> {
        val listOfImgs = ArrayList<String>()
        if (null == data.data) {
            val clipdata = data.clipData
            for (i in 0 until clipdata!!.itemCount) {
                val selectedImage = clipdata.getItemAt(i).uri
                val selectedImagePath = FileProcessing.getPath(context, selectedImage)
                if (selectedImagePath != null) {
                    listOfImgs.add(selectedImagePath)
                }
            }
        }
        return listOfImgs
    }

}


