cmake_minimum_required(VERSION 3.22.1)

project("edgeviewer")

add_library( # Name of the library.
        native-lib

        # Shared library.
        SHARED

        # Your source files.
        native-lib.cpp)

# Adjust this path to your unpacked OpenCV Android SDK
set(OpenCV_DIR "${CMAKE_SOURCE_DIR}/../opencv/sdk/native/jni")  # example

find_package(OpenCV REQUIRED)

message(STATUS "OpenCV libraries: ${OpenCV_LIBS}")

target_link_libraries(
        native-lib
        ${OpenCV_LIBS}
        android
        log
)