package gl

import android.graphics.Bitmap
import java.nio.ByteBuffer

object NativeLib {

    init {
        System.loadLibrary("native-lib")
    }

    external fun processFrameRgba(input: ByteArray, width: Int, height: Int): ByteArray

    fun processBitmap(src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height
        val buffer = ByteBuffer.allocate(width * height * 4)
        src.copyPixelsToBuffer(buffer)
        val inputBytes = buffer.array()
        val outputBytes = processFrameRgba(inputBytes, width, height)
        val outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        outBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(outputBytes))
        return outBitmap
    }
}