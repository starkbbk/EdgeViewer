package com.example.edgeviewer.gl

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.edgeviewer.R   // <- IMPORTANT: this R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Uses res/layout/activity_main.xml
        setContentView(R.layout.activity_main)

        // This MUST find the view from activity_main.xml
        val glView = findViewById<EdgeGLSurfaceView>(R.id.glView)
            ?: throw IllegalStateException("glView not found in activity_main.xml")

        // Load test.png from res/drawable
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.test)
            ?: throw IllegalStateException("R.drawable.test could not be decoded")

        glView.setBitmap(bitmap)
    }
}