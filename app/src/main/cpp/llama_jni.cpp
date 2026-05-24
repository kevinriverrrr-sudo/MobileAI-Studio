#include "llama_jni.h"
#include <android/log.h>
#include <string>
#include <sstream>
#include <chrono>

#define LOG_TAG "LlamaCppEngine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Stub engine state (placeholder until real llama.cpp is linked)
typedef struct {
    bool initialized;
    bool model_loaded;
    std::string model_path;
    std::string model_name;
    int context_size;
    int gpu_layers;
    int vocab_size;
    // Placeholder for actual llama.cpp state
} EngineState;

static EngineState* get_state(jlong handler) {
    return reinterpret_cast<EngineState*>(handler);
}

JNIEXPORT jlong JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeInitEngine(
    JNIEnv *env, jobject thiz) {
    LOGI("Initializing LlamaCppEngine...");
    EngineState* state = new EngineState();
    state->initialized = true;
    state->model_loaded = false;
    state->context_size = 4096;
    state->gpu_layers = 0;
    state->vocab_size = 32000;
    LOGI("Engine initialized successfully (stub mode)");
    return reinterpret_cast<jlong>(state);
}

JNIEXPORT jboolean JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeLoadModel(
    JNIEnv *env, jobject thiz, jlong handler, jstring model_path) {
    EngineState* state = get_state(handler);
    if (!state || !state->initialized) {
        LOGE("Engine not initialized");
        return false;
    }

    const char* path = env->GetStringUTFChars(model_path, nullptr);
    LOGI("Loading model from: %s", path);

    // In production, this would call:
    // llama_model_load_from_file(path, llama_context_default_params());

    state->model_path = path;
    state->model_loaded = true;

    // Extract model name from path
    std::string p(path);
    size_t lastSlash = p.find_last_of("/");
    if (lastSlash != std::string::npos) {
        state->model_name = p.substr(lastSlash + 1);
    } else {
        state->model_name = p;
    }

    env->ReleaseStringUTFChars(model_path, path);
    LOGI("Model loaded: %s (stub)", state->model_name.c_str());
    return true;
}

JNIEXPORT void JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeUnloadModel(
    JNIEnv *env, jobject thiz, jlong handler) {
    EngineState* state = get_state(handler);
    if (state) {
        LOGI("Unloading model: %s", state->model_name.c_str());
        state->model_loaded = false;
        state->model_path.clear();
        state->model_name.clear();
    }
}

JNIEXPORT jboolean JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeIsModelLoaded(
    JNIEnv *env, jobject thiz, jlong handler) {
    EngineState* state = get_state(handler);
    return state && state->model_loaded;
}

JNIEXPORT void JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeReleaseEngine(
    JNIEnv *env, jobject thiz, jlong handler) {
    EngineState* state = get_state(handler);
    if (state) {
        LOGI("Releasing engine");
        delete state;
    }
}

JNIEXPORT jstring JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeStartCompletion(
    JNIEnv *env, jobject thiz, jlong handler, jstring prompt, jfloat temperature,
    jfloat top_p, jint top_k, jint max_tokens) {
    EngineState* state = get_state(handler);

    if (!state || !state->model_loaded) {
        return env->NewStringUTF("Ошибка: модель не загружена. Скачайте GGUF модель и загрузите её.");
    }

    const char* promptStr = env->GetStringUTFChars(prompt, nullptr);
    LOGI("Starting completion: %.50s... (temp=%.2f, top_p=%.2f, top_k=%d, max=%d)",
         promptStr, temperature, top_p, top_k, max_tokens);

    // In production, this would call llama_decode() + llama_sample_*()
    // For now, generate a stub response with simulated timing

    std::ostringstream response;
    response << "Движок llama.cpp работает в заглушечном режиме (stub).\n\n";
    response << "Для полноценной инференса:\n";
    response << "1. Скачайте GGUF модель с HuggingFace\n";
    response << "2. Загрузите её через раздел 'Мои модели'\n";
    response << "3. Начните чат\n\n";
    response << "Запрос: ";
    response << promptStr;
    response << "\n\n";
    response << "Модель: ";
    response << state->model_name;
    response << "\n";
    response << "Контекст: ";
    response << state->context_size;
    response << " токенов\n";
    response << "GPU слои: ";
    response << state->gpu_layers;
    response << "\n";

    env->ReleaseStringUTFChars(prompt, promptStr);
    return env->NewStringUTF(response.str().c_str());
}

JNIEXPORT jint JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeGetTokenCount(
    JNIEnv *env, jobject thiz, jlong handler, jstring text) {
    // Stub: estimate ~4 chars per token
    const char* str = env->GetStringUTFChars(text, nullptr);
    int count = 0;
    while (str[count] != '\0') count++;
    env->ReleaseStringUTFChars(text, str);
    return count / 4 + 1;
}

JNIEXPORT jlong JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeGetAvailableRAM(
    JNIEnv *env, jobject thiz) {
    // Read from /proc/meminfo
    FILE* f = fopen("/proc/meminfo", "r");
    if (!f) return 4096LL * 1024 * 1024;

    char line[256];
    long available = 0;
    while (fgets(line, sizeof(line), f)) {
        if (strncmp(line, "MemAvailable:", 13) == 0) {
            sscanf(line + 13, "%ld kB", &available);
            break;
        }
    }
    fclose(f);
    return available * 1024LL;
}

JNIEXPORT jlong JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeGetAvailableVRAM(
    JNIEnv *env, jobject thiz) {
    // Stub: estimate based on typical mobile GPU
    return 1024LL * 1024 * 1024; // 1GB estimated
}

JNIEXPORT jstring JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeGetModelInfo(
    JNIEnv *env, jobject thiz, jlong handler) {
    EngineState* state = get_state(handler);
    if (!state) return env->NewStringUTF("Engine not initialized");

    std::ostringstream info;
    info << "Model: " << (state->model_name.empty() ? "None" : state->model_name) << "\n";
    info << "Loaded: " << (state->model_loaded ? "Yes" : "No") << "\n";
    info << "Context: " << state->context_size << "\n";
    info << "GPU Layers: " << state->gpu_layers << "\n";
    info << "Vocab: " << state->vocab_size << "\n";
    info << "Mode: Stub (link real llama.cpp for inference)\n";

    return env->NewStringUTF(info.str().c_str());
}

JNIEXPORT void JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeSetContextSize(
    JNIEnv *env, jobject thiz, jlong handler, jint size) {
    EngineState* state = get_state(handler);
    if (state) state->context_size = size;
}

JNIEXPORT void JNICALL
Java_com_mobileaistudio_inference_LlamaCppEngine_nativeSetGPULayers(
    JNIEnv *env, jobject thiz, jlong handler, jint layers) {
    EngineState* state = get_state(handler);
    if (state) state->gpu_layers = layers;
}
