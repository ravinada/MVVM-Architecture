package com.ravinada.mvvmarchitecture.images

import android.os.Environment
import java.io.Serializable

class ImageConfig : Serializable {

    var extension: ImagePicker.Extension = ImagePicker.Extension.PNG
    var compressLevel: ImagePicker.ComperesLevel
    var mode: ImagePicker.Mode
    var directory: String
    var reqHeight: Int = 0
    var reqWidth: Int = 0
    var allowMultiple: Boolean = false
    var isImgFromCamera: Boolean = false
    var allowOnlineImages: Boolean = false
    var debug: Boolean = false

    init {
        this.compressLevel = ImagePicker.ComperesLevel.NONE
        this.mode = ImagePicker.Mode.CAMERA
        this.directory = Environment.getExternalStorageDirectory().toString() + ImageTags.Tags.IMAGE_PICKER_DIR
        this.reqHeight = 0
        this.reqWidth = 0
        this.allowMultiple = false
        this.allowOnlineImages = false
    }

    override fun toString(): String {
        return "ImageConfig{" +
                "extension=" + extension +
                ", compressLevel=" + compressLevel +
                ", mode=" + mode +
                ", directory='" + directory + '\''.toString() +
                ", reqHeight=" + reqHeight +
                ", reqWidth=" + reqWidth +
                ", allowMultiple=" + allowMultiple +
                ", isImgFromCamera=" + isImgFromCamera +
                ", allowOnlineImages=" + allowOnlineImages +
                ", debug=" + debug +
                '}'.toString()
    }
}
