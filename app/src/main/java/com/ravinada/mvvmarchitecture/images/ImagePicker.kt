package com.ravinada.mvvmarchitecture.images

import android.annotation.TargetApi
import android.os.Build
import android.os.Environment
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

class ImagePicker
/**
 * Instantiates a new Image picker.
 *
 * @param builder the builder
 */
(builder: Builder) {

    private val imageConfig: ImageConfig

    init {
        // Required
        val context = builder.context
        // Optional
        imageConfig = builder.imageConfig
        val callingIntent = context.get()?.let {
            ImageActivity.getCallingIntent(it.requireContext(), imageConfig)
        }

        context.get()?.startActivityForResult(callingIntent, IMAGE_PICKER_REQUEST_CODE)
    }


    /**
     * The type Builder.
     */
    class Builder
    /**
     * Instantiates a new Builder.
     *
     * @param context the context
     */
    (context: Fragment) : ImagePickerBuilderBase {

        // Required params
        val context: WeakReference<Fragment>

        val imageConfig: ImageConfig

        init {
            this.context = WeakReference(context)
            this.imageConfig = ImageConfig()
        }

        override fun compressLevel(compressLevel: ComperesLevel): Builder {
            this.imageConfig.compressLevel = compressLevel
            return this
        }

        override fun mode(mode: Mode): Builder {
            this.imageConfig.mode = mode
            return this
        }

        override fun directory(directory: String): Builder {
            this.imageConfig.directory = directory
            return this
        }

        override fun directory(directory: Directory): Builder {
            when (directory) {
                Directory.DEFAULT -> this.imageConfig.directory = Environment.getExternalStorageDirectory().toString() + ImageTags.Tags.IMAGE_PICKER_DIR
                else -> {
                }
            }
            return this
        }

        override fun extension(extension: Extension): Builder {
            this.imageConfig.extension = extension
            return this
        }

        override fun scale(minWidth: Int, minHeight: Int): Builder {
            this.imageConfig.reqHeight = minHeight
            this.imageConfig.reqWidth = minWidth
            return this
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        override fun allowMultipleImages(allowMultiple: Boolean): Builder {
            this.imageConfig.allowMultiple = allowMultiple
            return this
        }

        override fun enableDebuggingMode(debug: Boolean): Builder {
            this.imageConfig.debug = debug
            return this
        }

        override fun allowOnlineImages(allowOnlineImages: Boolean): Builder {
            this.imageConfig.allowOnlineImages = allowOnlineImages
            return this
        }


        override fun build(): ImagePicker {
            return ImagePicker(this)
        }




    }


    /**
     * The enum Extension.
     */
    enum class Extension private constructor(
            /**
             * Gets value.
             *
             * @return the value
             */
            val value: String) {
        /**
         * Png extension.
         */
        PNG(".png"),
        /**
         * Jpg extension.
         */
        JPG(".jpg")
    }

    /**
     * The enum Comperes level.
     */
    enum class ComperesLevel private constructor(
            /**
             * Gets value.
             *
             * @return the value
             */
            val value: Int) {
        /**
         * Hard comperes level.
         */
        HARD(20),
        /**
         * Medium comperes level.
         */
        MEDIUM(50),
        /**
         * Soft comperes level.
         */
        SOFT(80),
        /**
         * None comperes level.
         */
        NONE(100);


        companion object {

            /**
             * Gets enum.
             *
             * @param value the value
             * @return the enum
             */
            fun getEnum(value: Int): ComperesLevel {
                for (v in values())
                    if (v.value == value) return v
                throw IllegalArgumentException()
            }
        }
    }

    /**
     * The enum Mode.
     */
    enum class Mode private constructor(
            /**
             * Gets value.
             *
             * @return the value
             */
            val value: Int) {
        /**
         * Camera mode.
         */
        CAMERA(0),
        /**
         * Gallery mode.
         */
        GALLERY(1),
        /**
         * Camera and gallery mode.
         */
        CAMERA_AND_GALLERY(2)
    }

    /**
     * The enum Directory.
     */
    enum class Directory private constructor(
            /**
             * Gets value.
             *
             * @return the value
             */
            val value: Int) {
        /**
         * Default directory.
         */
        DEFAULT(0)
    }

    companion object {

        /**
         * The constant IMAGE_PICKER_REQUEST_CODE.
         */
        val IMAGE_PICKER_REQUEST_CODE = 42141
        /**
         * The constant EXTRA_IMAGE_PATH.
         */
        val EXTRA_IMAGE_PATH = "EXTRA_IMAGE_PATH"
    }
}
