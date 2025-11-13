#include <jni.h>
#include <vector>
#include <cstring>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_edgeviewer_NativeLib_processFrameRgba(
        JNIEnv *env,
        jobject /*thiz*/,
        jbyteArray input_,
        jint width,
        jint height) {

    const int w = width;
    const int h = height;
    const int channels = 4;
    const int dataSize = w * h * channels;

    jbyte *inputBytes = env->GetByteArrayElements(input_, nullptr);
    if (inputBytes == nullptr) {
        return nullptr;
    }

    std::vector<unsigned char> inputData(dataSize);
    for (int i = 0; i < dataSize; ++i) {
        inputData[i] = static_cast<unsigned char>(inputBytes[i]);
    }

    cv::Mat rgba(h, w, CV_8UC4, inputData.data());
    cv::Mat gray, edges, edgesRgba;

    cv::cvtColor(rgba, gray, cv::COLOR_RGBA2GRAY);
    cv::Canny(gray, edges, 80, 150);
    cv::cvtColor(edges, edgesRgba, cv::COLOR_GRAY2RGBA);

    std::vector<unsigned char> outputData(dataSize);
    std::memcpy(outputData.data(), edgesRgba.data, dataSize);

    env->ReleaseByteArrayElements(input_, inputBytes, JNI_ABORT);

    jbyteArray result = env->NewByteArray(dataSize);
    if (result != nullptr) {
        env->SetByteArrayRegion(result, 0, dataSize, reinterpret_cast<jbyte *>(outputData.data()));
    }
    return result;
}
