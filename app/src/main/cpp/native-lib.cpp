#include <jni.h>
#include <android/bitmap.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>

#define LOG_TAG "EdgeViewerNative"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
Java_com_example_edgeviewer_gl_NativeLib_processFrame(
        JNIEnv *env,
        jobject /* this */,
        jobject inputBitmap,
        jobject outputBitmap) {

    AndroidBitmapInfo inInfo;
    AndroidBitmapInfo outInfo;
    void *inPixels = nullptr;
    void *outPixels = nullptr;

    if (AndroidBitmap_getInfo(env, inputBitmap, &inInfo) < 0 ||
        AndroidBitmap_getInfo(env, outputBitmap, &outInfo) < 0) {
        LOGE("Cannot get bitmap info");
        return;
    }

    if (inInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888 ||
        outInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Only RGBA_8888 is supported");
        return;
    }

    if (AndroidBitmap_lockPixels(env, inputBitmap, &inPixels) < 0 ||
        AndroidBitmap_lockPixels(env, outputBitmap, &outPixels) < 0) {
        LOGE("Cannot lock pixels");
        return;
    }

    // Wrap into OpenCV Mats
    cv::Mat inRgba(inInfo.height, inInfo.width, CV_8UC4, inPixels);
    cv::Mat outRgba(outInfo.height, outInfo.width, CV_8UC4, outPixels);

    // Convert to gray
    cv::Mat gray;
    cv::cvtColor(inRgba, gray, cv::COLOR_RGBA2GRAY);

    // Canny edge detection
    cv::Mat edges;
    cv::Canny(gray, edges, 50, 150);

    // Convert edges back to RGBA (white edges on black background)
    cv::Mat edgesRgba;
    cv::cvtColor(edges, edgesRgba, cv::COLOR_GRAY2RGBA);

    edgesRgba.copyTo(outRgba);

    AndroidBitmap_unlockPixels(env, inputBitmap);
    AndroidBitmap_unlockPixels(env, outputBitmap);
}