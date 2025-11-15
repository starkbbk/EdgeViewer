package com.example.edgeviewer.gl

import android.graphics.Bitmap

object NativeLib {

    init {
        System.loadLibrary("native-lib")
    }

    /**
     * Process bitmap with OpenCV C++ (Canny edge or grayscale).
     * Input and output must be ARGB_8888.
     */
    external fun processFrame(
        inputBitmap: Bitmap,
        outputBitmap: Bitmap
    )
}