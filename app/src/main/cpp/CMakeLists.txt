cmake_minimum_required(VERSION 3.22.1)
project("llama_android" LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)

# Find Android's log library
find_library(log-lib log)

# Create shared library
add_library(
    llama_android
    SHARED
    llama_jni.cpp
)

# Add include directories
target_include_directories(llama_android PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})

# Link libraries
target_link_libraries(
    llama_android
    ${log-lib}
    android
    jnigraphics
)

# Compile flags for ARM
if(ANDROID_ABI STREQUAL "arm64-v8a")
    target_compile_options(llama_android PRIVATE
        -march=armv8-a+fp16
        -O3
        -DNDEBUG
    )
endif()
