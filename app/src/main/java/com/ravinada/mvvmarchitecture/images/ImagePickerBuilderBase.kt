package com.ravinada.mvvmarchitecture.images

interface ImagePickerBuilderBase {
    /**
     * Compress level image picker . builder.
     *
     * @param compressLevel the compress level
     * @return the image picker . builder
     */
    fun compressLevel(compressLevel: ImagePicker.ComperesLevel): ImagePicker.Builder

    /**
     * Mode image picker . builder.
     *
     * @param mode the mode
     * @return the image picker . builder
     */
    fun mode(mode: ImagePicker.Mode): ImagePicker.Builder

    /**
     * Directory image picker . builder.
     *
     * @param directory the directory
     * @return the image picker . builder
     */
    fun directory(directory: String): ImagePicker.Builder

    /**
     * Directory image picker . builder.
     *
     * @param directory the directory
     * @return the image picker . builder
     */
    fun directory(directory: ImagePicker.Directory): ImagePicker.Builder

    /**
     * Extension image picker . builder.
     *
     * @param extension the extension
     * @return the image picker . builder
     */
    fun extension(extension: ImagePicker.Extension): ImagePicker.Builder

    /**
     * Scale image picker . builder.
     *
     * @param minWidth  the min width
     * @param minHeight the min height
     * @return the image picker . builder
     */
    fun scale(minWidth: Int, minHeight: Int): ImagePicker.Builder

    /**
     * Allow multiple images
     *
     * @param allowMultiple the allow multiple
     * @return the image picker . builder
     */
    fun allowMultipleImages(allowMultiple: Boolean): ImagePicker.Builder

    /**
     * Enable debugging mode image picker . builder.
     *
     * @param debug the debug
     * @return the image picker . builder
     */
    fun enableDebuggingMode(debug: Boolean): ImagePicker.Builder

    /**
     * Allow online images (ex: Google Drive, Google Photo ...)
     *
     * @param allowOnlineImages the allow online images
     * @return the image picker . builder
     */
    fun allowOnlineImages(allowOnlineImages: Boolean): ImagePicker.Builder

    /**
     * Build image picker.
     *
     * @return the image picker
     */
    fun build(): ImagePicker

}
