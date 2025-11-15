package com.example.edgeviewer.gl

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.media.ImageReader
import android.os.Bundle
import android.util.Size
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.hardware.camera2.*

class MainActivity : AppCompatActivity() {

    private lateinit var glView: EdgeGLSurfaceView

    private lateinit var cameraManager: CameraManager
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null

    private val cameraId: String by lazy {
        cameraManager.cameraIdList.first()      // back camera
    }

    companion object {
        private const val REQ_CAMERA = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glView = findViewById(R.id.glView)
        cameraManager = getSystemService(CameraManager::class.java)

        // Fallback: show test.png if camera permission not yet granted
        val fallback = BitmapFactory.decodeResource(resources, R.drawable.test)
        glView.setBitmap(fallback)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQ_CAMERA
            )
        } else {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CAMERA &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        }
    }

    private fun startCamera() {
        imageReader = ImageReader.newInstance(
            640,
            480,
            ImageFormat.YUV_420_888,
            2
        ).apply {
            setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener

                // Simple YUV→JPEG→Bitmap conversion (not super fast, but OK for demo)
                val nv21 = YuvUtils.yuv420ToNv21(image)
                image.close()

                val bitmap = YuvUtils.nv21ToBitmap(this@MainActivity, nv21, Size(640, 480))

                // Prepare output bitmap for JNI
                val outBitmap = Bitmap.createBitmap(
                    bitmap.width,
                    bitmap.height,
                    Bitmap.Config.ARGB_8888
                )

                // Native OpenCV processing
                NativeLib.processFrame(bitmap, outBitmap)

                // Push to GL
                glView.setBitmap(outBitmap)

            }, null)
        }

        cameraManager.openCamera(
            cameraId,
            object : CameraDevice.StateCallback() {
                override fun onOpened(device: CameraDevice) {
                    cameraDevice = device
                    createCaptureSession()
                }

                override fun onDisconnected(device: CameraDevice) {
                    device.close()
                    cameraDevice = null
                }

                override fun onError(device: CameraDevice, error: Int) {
                    device.close()
                    cameraDevice = null
                }
            },
            null
        )
    }

    private fun createCaptureSession() {
        val device = cameraDevice ?: return
        val readerSurface: Surface = imageReader!!.surface

        device.createCaptureSession(
            listOf(readerSurface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session

                    val requestBuilder = device.createCaptureRequest(
                        CameraDevice.TEMPLATE_PREVIEW
                    ).apply {
                        addTarget(readerSurface)
                        set(CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                    }

                    session.setRepeatingRequest(requestBuilder.build(), null, null)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    // ignore for now
                }
            },
            null
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        captureSession?.close()
        cameraDevice?.close()
        imageReader?.close()
    }
}