package com.example.edgeviewer.gl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class EdgeRenderer : GLSurfaceView.Renderer {

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        void main() {
            gl_Position = vPosition;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """

    private var program: Int = 0

    private val triangleCoords = floatArrayOf(
        0.0f,  0.622008459f, 0.0f,   // top
       -0.5f, -0.311004243f, 0.0f,   // bottom left
        0.5f, -0.311004243f, 0.0f    // bottom right
    )

    private val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private var positionHandle: Int = 0
    private var colorHandle: Int = 0

    private val vertexCount = triangleCoords.size / 3
    private val vertexStride = 3 * 4 // 3 coords * 4 bytes per float

    private val vertexBuffer = java.nio.ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
        order(java.nio.ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(triangleCoords)
            position(0)
        }
    }

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    override fun onDrawFrame(unused: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glUseProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(it, 3, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)
        }

        colorHandle = GLES20.glGetUniformLocation(program, "vColor").also {
            GLES20.glUniform4fv(it, 1, color, 0)
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}