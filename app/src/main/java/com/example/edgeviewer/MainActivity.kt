package com.example.edgeviewer

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.edgeviewer.gl.EdgeGLSurfaceView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val glView = EdgeGLSurfaceView(this)
        setContentView(glView)

        val bmp = BitmapFactory.decodeResource(resources, R.drawable.sample)
        val processed = NativeLib.processBitmap(bmp)
        glView.setBitmap(processed)
    }
}
