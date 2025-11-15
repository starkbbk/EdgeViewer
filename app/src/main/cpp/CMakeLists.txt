cmake_minimum_required(VERSION 3.10.2)
project("edgeviewer")

# Path to your OpenCV Android SDK
set(OpenCV_DIR "/Users/starkbbks/Downloads/OpenCV-android-sdk/sdk/native/jni")

find_package(OpenCV REQUIRED)

add_library(
        native-lib
        SHARED
        processor.cpp)

find_library(
        log-lib
        log)

target_link_libraries(
        native-lib
        ${OpenCV_LIBS}
        ${log-lib}
        c++_shared)