cmake_minimum_required(VERSION 3.10.2)

project(edgeviewer)

# Use C++17
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(
        native-lib
        SHARED
        native-lib.cpp
)


find_library(
        log-lib
        log
)

target_link_libraries(
        native-lib
        ${log-lib}
        c++_shared
)