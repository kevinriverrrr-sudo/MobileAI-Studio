#ifndef LLAMA_JNI_H
#define LLAMA_JNI_H

#include <jni.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeInitEngine(
    JNIEnv *env, jobject thiz);

JNIEXPORT jboolean JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeLoadModel(
    JNIEnv *env, jobject thiz, jlong handler, jstring model_path);

JNIEXPORT void JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeUnloadModel(
    JNIEnv *env, jobject thiz, jlong handler);

JNIEXPORT jboolean JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeIsModelLoaded(
    JNIEnv *env, jobject thiz, jlong handler);

JNIEXPORT void JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeReleaseEngine(
    JNIEnv *env, jobject thiz, jlong handler);

JNIEXPORT jstring JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeStartCompletion(
    JNIEnv *env, jobject thiz, jlong handler, jstring prompt, jfloat temperature,
    jfloat top_p, jint top_k, jint max_tokens);

JNIEXPORT jint JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeGetTokenCount(
    JNIEnv *env, jobject thiz, jlong handler, jstring text);

JNIEXPORT jlong JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeGetAvailableRAM(
    JNIEnv *env, jobject thiz);

JNIEXPORT jlong JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeGetAvailableVRAM(
    JNIEnv *env, jobject thiz);

JNIEXPORT jstring JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeGetModelInfo(
    JNIEnv *env, jobject thiz, jlong handler);

JNIEXPORT void JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeSetContextSize(
    JNIEnv *env, jobject thiz, jlong handler, jint size);

JNIEXPORT void JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeSetGPULayers(
    JNIEnv *env, jobject thiz, jlong handler, jint layers);

#ifdef __cplusplus
}
#endif

#endif // LLAMA_JNI_H
