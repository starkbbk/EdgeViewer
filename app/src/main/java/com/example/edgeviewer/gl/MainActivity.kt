package com.example.edgeviewer.gl

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.edgeviewer.R   // ⭐ REQUIRED FIX
import com.example.edgeviewer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load test.png as fallback
        val fallback = BitmapFactory.decodeResource(resources, R.drawable.test)

        // Set bitmap to renderer (your logic)
        binding.glView.setBitmap(fallback)
    }
}