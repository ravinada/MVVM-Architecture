package com.ravinada.mvvmarchitecture.images

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.ravinada.mvvmarchitecture.R
import com.ravinada.mvvmarchitecture.base.BaseEventBusModel
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.lang.ref.WeakReference
import java.util.*

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class ImageActivity : AppCompatActivity() {
    private var destination: File? = null
    private lateinit var mImageUri: Uri
    private lateinit var mImgConfig: ImageConfig
    private lateinit var listOfImgs: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent != null) {
            mImgConfig = intent.getSerializableExtra(ImageTags.Tags.IMG_CONFIG) as ImageConfig
        }

        if (savedInstanceState == null) {
            pickImageWrapper()
            listOfImgs = ArrayList()
        }
        if (mImgConfig.debug)
            Log.d(ImageTags.Tags.TAG, mImgConfig.toString())
    }

    private fun pickImage() {
        Utility.createFolder(mImgConfig.directory)
        destination =
            File(mImgConfig.directory, Utility.getRandomString() + mImgConfig.extension.value)
        when (mImgConfig.mode) {
            ImagePicker.Mode.CAMERA -> startActivityFromCamera()
            ImagePicker.Mode.GALLERY -> if (mImgConfig.allowMultiple && mImgConfig.allowOnlineImages)
                startActivityFromGalleryMultiImg()
            else
                startActivityFromGallery()
            ImagePicker.Mode.CAMERA_AND_GALLERY -> showFromCameraOrGalleryAlert()
        }
    }

    private fun showFromCameraOrGalleryAlert() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.media_picker_select_from))
            .setPositiveButton(getString(R.string.media_picker_camera)) { _, i ->
                if (mImgConfig.debug)
                    startActivityFromCamera()
            }
            .setNegativeButton(getString(R.string.media_picker_gallery)) { _, i ->
                if (mImgConfig.debug)
                    if (mImgConfig.allowMultiple)
                        startActivityFromGalleryMultiImg()
                    else
                        startActivityFromGallery()
            }
            .setOnCancelListener {
                if (mImgConfig.debug)
                    finish()

                EventBus.getDefault()
                    .post(BaseEventBusModel(resources.getString(R.string.close_video_photo_pop_up)))
            }
            .show()
    }

    private fun startActivityFromGallery() {
        mImgConfig.isImgFromCamera = false
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, !mImgConfig.allowOnlineImages)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, ImageTags.IntentCode.REQUEST_CODE_SELECT_PHOTO)
    }

    private fun startActivityFromGalleryMultiImg() {
        mImgConfig.isImgFromCamera = false
        val photoPickerIntent = Intent()
        photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, !mImgConfig.allowOnlineImages)
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        photoPickerIntent.action = Intent.ACTION_GET_CONTENT
        photoPickerIntent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(photoPickerIntent, "Select Picture"), ImageTags.IntentCode.REQUEST_CODE_SELECT_MULTI_PHOTO
        )
    }

    private fun startActivityFromCamera() {
        mImgConfig.isImgFromCamera = true
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        mImageUri = FileProvider.getUriForFile(
            this,
            applicationContext.packageName + ".provider",
            destination!!
        )
//        contentResolver.openInputStream(mImageUri)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"), ImageTags.IntentCode.CAMERA_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                    ImageTags.IntentCode.CAMERA_REQUEST -> CompressImageTask(destination?.absolutePath, mImgConfig, this@ImageActivity).execute()
                    ImageTags.IntentCode.REQUEST_CODE_SELECT_PHOTO -> processOneImage(data)
                    ImageTags.IntentCode.REQUEST_CODE_SELECT_MULTI_PHOTO -> processMultiPhoto(data)
                    else -> {
                    }
                }

        } else {
            val intent = Intent()
            intent.action = "net.alhazmy13.mediapicker.rxjava.image.service"
            intent.putExtra(ImageTags.Tags.PICK_ERROR, "user did not select any image")
            sendBroadcast(intent)
            finish()
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun processMultiPhoto(data: Intent?) {
        if(data!=null)
        {
            if (data.clipData == null) {
                processOneImage(data)
            } else {
                //intent has multi images
                listOfImgs = ImageProcessing.processMultiImage(this, data) as ArrayList<String>
                if (listOfImgs.size > 0) {
                    CompressImageTask(listOfImgs, mImgConfig, this@ImageActivity).execute()
                } else {
                    //For 'Select pic from Google Photos - app Crash' fix
                    val check = data.clipData?.toString()
                    if (check != null) {
                        if (check.contains("com.google.android.apps.photos")) {
                            val clipData = data.clipData
                            for (i in 0 until clipData!!.itemCount) {
                                val selectedImage = clipData.getItemAt(i).uri
                                val selectedImagePath = FileProcessing.getPath(this@ImageActivity, selectedImage)
                                listOfImgs.add(selectedImagePath.toString())
                            }
                            CompressImageTask(listOfImgs, mImgConfig, this@ImageActivity).execute()
                        }
                    }
                }
            }
        }

    }

    private fun processOneImage(data: Intent?) {
        if(data!=null)
        {
            try {
                val selectedImage = data.data
                val selectedImagePath = selectedImage?.let { FileProcessing.getPath(this, it) }
                CompressImageTask(
                    selectedImagePath.toString(),
                    mImgConfig, this@ImageActivity
                ).execute()
            } catch (ex: Exception) {
            }
        }


    }

    private fun finishActivity(path: List<String>?) {
        val resultIntent = Intent()
        resultIntent.putExtra(ImagePicker.EXTRA_IMAGE_PATH, path as Serializable?)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun pickImageWrapper() {
        if (Build.VERSION.SDK_INT >= 23) {
            val permissionsNeeded = ArrayList<String>()
            val permissionsList = ArrayList<String>()
            if ((mImgConfig.mode == ImagePicker.Mode.CAMERA || mImgConfig.mode == ImagePicker.Mode.CAMERA_AND_GALLERY) && !addPermission(
                    permissionsList,
                    Manifest.permission.CAMERA
                )
            )
                permissionsNeeded.add(getString(R.string.media_picker_camera))
            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add(getString(R.string.media_picker_read_Write_external_storage))

            if (permissionsList.size > 0) {
                if (permissionsNeeded.size > 0) {
                    // Need Rationale
                    var message =
                        getString(R.string.media_picker_you_need_to_grant_access_to) + permissionsNeeded[0]
                    for (i in 1 until permissionsNeeded.size)
                        message = message + ", " + permissionsNeeded[i]
                    showMessageOKCancel(message) { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this@ImageActivity,
                            permissionsList.toTypedArray(), ImageTags.IntentCode.REQUEST_CODE_ASK_PERMISSIONS
                        )
                    }
                    return
                }
                ActivityCompat.requestPermissions(
                    this@ImageActivity, permissionsList.toTypedArray(), ImageTags.IntentCode.REQUEST_CODE_ASK_PERMISSIONS
                )
                return
            }

            pickImage()
        } else {
            pickImage()
        }
    }

    private fun showMessageOKCancel(message: String, okListener: (Any, Any) -> Unit) {
        AlertDialog.Builder(this@ImageActivity)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.media_picker_ok), okListener)
            .setNegativeButton(getString(R.string.media_picker_cancel)) { _, _ ->
                EventBus.getDefault()
                    .post(BaseEventBusModel(resources.getString(R.string.close_video_photo_pop_up)))
                finish()
            }.create()
            .show()
    }

    private fun addPermission(permissionsList: MutableList<String>, permission: String): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this@ImageActivity,
                permission
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(permission)
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this@ImageActivity,
                    permission
                )
            )
                return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            ImageTags.IntentCode.REQUEST_CODE_ASK_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                // Initial
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED
                // Fill with results
                for (i in permissions.indices)
                    perms[permissions[i]] = grantResults[i]
                // Check for ACCESS_FINE_LOCATION
                if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    pickImage()
                } else {
                    finish()
                    EventBus.getDefault()
                        .post(BaseEventBusModel(resources.getString(R.string.close_video_photo_pop_up)))
                    // Permission Denied
                    Toast.makeText(
                        this@ImageActivity,
                        getString(R.string.media_picker_some_permission_is_denied),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class CompressImageTask : AsyncTask<Void, Void, String> {

        private val mImgConfig: ImageConfig?
        private val listOfImgs: List<String>
        private var destinationPaths: MutableList<String>? = null
        private var mContext: WeakReference<ImageActivity>? = null


        internal constructor(
            listOfImgs: List<String>,
            imageConfig: ImageConfig,
            context: ImageActivity
        ) {
            this.listOfImgs = listOfImgs
            this.mContext = WeakReference(context)
            this.mImgConfig = imageConfig
            this.destinationPaths = ArrayList()
        }

        internal constructor(absolutePath: String?, imageConfig: ImageConfig?, context: ImageActivity)
        {
            val list = ArrayList<String>()
            absolutePath?.let { list.add(it) }
            this.listOfImgs = list
            this.mContext = WeakReference(context)
            this.destinationPaths = ArrayList()
            this.mImgConfig = imageConfig
        }


        override fun doInBackground(vararg params: Void): String? {
            for (mPath in listOfImgs) {
                val file = File(mPath)
                val destinationFile: File
                destinationFile = if (mImgConfig!!.isImgFromCamera) {
                    file
                } else {
                    File(
                        mImgConfig.directory,
                        Utility.getRandomString() + mImgConfig.extension.value
                    )
                }
                destinationPaths!!.add(destinationFile.absolutePath)
                try {
                    Utility.compressAndRotateIfNeeded(
                        file,
                        destinationFile,
                        mImgConfig.compressLevel.value,
                        mImgConfig.reqWidth,
                        mImgConfig.reqHeight
                    )
                } catch (e: IOException) {
                }
            }
            return ""
        }

        override fun onPostExecute(aVoid: String) {
            super.onPostExecute("")

            val context = mContext!!.get()
            if (context != null) {
                context.finishActivity(destinationPaths)
                val intent = Intent()
                intent.action = "net.alhazmy13.mediapicker.rxjava.image.service"
                intent.putExtra(ImageTags.Tags.IMAGE_PATH, destinationPaths as Serializable?)
                intent.putExtra(ImageTags.Tags.IS_CAMERA, true)
                context.sendBroadcast(intent)
            }
        }
    }

    companion object {
        fun getCallingIntent(context: Context, imageConfig: ImageConfig): Intent {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra(ImageTags.Tags.IMG_CONFIG, imageConfig)
            return intent
        }
    }
}
