package com.example.edgeviewer.gl

import android.content.Context
import android.graphics.*
import android.media.Image
import android.util.Size
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

object YuvUtils {

    fun yuv420ToNv21(image: Image): ByteArray {
        val width = image.width
        val height = image.height
        val ySize = width * height
        val uvSize = width * height / 2

        val nv21 = ByteArray(ySize + uvSize)

        val yBuffer: ByteBuffer = image.planes[0].buffer
        val uBuffer: ByteBuffer = image.planes[1].buffer
        val vBuffer: ByteBuffer = image.planes[2].buffer

        var rowStride = image.planes[0].rowStride
        var pos = 0

        // Y
        for (row in 0 until height) {
            yBuffer.position(row * rowStride)
            yBuffer.get(nv21, pos, width)
            pos += width
        }

        rowStride = image.planes[2].rowStride
        val pixelStride = image.planes[2].pixelStride

        // VU (NV21)
        for (row in 0 until height / 2) {
            var col = 0
            while (col < width) {
                val vIndex = row * rowStride + col * pixelStride
                nv21[pos++] = vBuffer.get(vIndex)
                nv21[pos++] = uBuffer.get(vIndex)
                col += 2
            }
        }

        return nv21
    }

    fun nv21ToBitmap(context: Context, nv21: ByteArray, size: Size): Bitmap {
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, size.width, size.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, size.width, size.height), 90, out)
        val jpegBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
    }
}