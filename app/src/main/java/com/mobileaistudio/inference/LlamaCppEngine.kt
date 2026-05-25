package com.mobileaistudio.inference

import kotlin.concurrent.Volatile

object LlamaCppEngine {

    init {
        try {
            System.loadLibrary("llama_android")
        } catch (e: UnsatisfiedLinkError) {
            // Native library not available - stub mode
        }
    }

    // --- Native methods ---

    private external fun nativeInitEngine(): Long
    private external fun nativeLoadModel(handler: Long, modelPath: String): Boolean
    private external fun nativeUnloadModel(handler: Long)
    private external fun nativeIsModelLoaded(handler: Long): Boolean
    private external fun nativeReleaseEngine(handler: Long)
    private external fun nativeStartCompletion(
        handler: Long, prompt: String, temperature: Float,
        topP: Float, topK: Int, maxTokens: Int
    ): String
    private external fun nativeGetTokenCount(handler: Long, text: String): Int
    private external fun nativeGetAvailableRAM(): Long
    private external fun nativeGetAvailableVRAM(): Long
    private external fun nativeGetModelInfo(handler: Long): String
    private external fun nativeSetContextSize(handler: Long, size: Int)
    private external fun nativeSetGPULayers(handler: Long, layers: Int)

    // --- Singleton instance (thread-safe) ---
    @Volatile
    private var engineHandler: Long = 0L
    @Volatile
    private var isInitialized = false
    private val lock = Any()

    fun initialize(): Boolean {
        if (isInitialized) return true
        synchronized(lock) {
            if (isInitialized) return true
            return try {
                engineHandler = nativeInitEngine()
                isInitialized = engineHandler != 0L
                isInitialized
            } catch (e: Throwable) {
                false
            }
        }
    }

    fun loadModel(modelPath: String): Boolean {
        if (!ensureInitialized()) return false
        return try {
            nativeLoadModel(engineHandler, modelPath)
        } catch (e: Throwable) {
            false
        }
    }

    fun unloadModel() {
        if (engineHandler != 0L) {
            try {
                nativeUnloadModel(engineHandler)
            } catch (_: Throwable) {}
        }
    }

    fun isModelLoaded(): Boolean {
        return if (engineHandler != 0L) {
            try { nativeIsModelLoaded(engineHandler) } catch (_: Throwable) { false }
        } else false
    }

    fun release() {
        synchronized(lock) {
            if (engineHandler != 0L) {
                try {
                    nativeReleaseEngine(engineHandler)
                } catch (_: Throwable) {}
                engineHandler = 0L
                isInitialized = false
            }
        }
    }

    fun complete(
        prompt: String,
        temperature: Float = 0.7f,
        topP: Float = 0.9f,
        topK: Int = 40,
        maxTokens: Int = 2048
    ): String {
        if (!ensureInitialized()) return "Error: Engine not initialized"
        return try {
            nativeStartCompletion(engineHandler, prompt, temperature, topP, topK, maxTokens)
        } catch (e: Throwable) {
            "Error: ${e.message}"
        }
    }

    fun getTokenCount(text: String): Int {
        return if (engineHandler != 0L) {
            try { nativeGetTokenCount(engineHandler, text) } catch (_: Throwable) { 0 }
        } else 0
    }

    fun getAvailableRAM(): Long {
        return try { nativeGetAvailableRAM() } catch (_: Throwable) { 0L }
    }

    fun getAvailableVRAM(): Long {
        return try { nativeGetAvailableVRAM() } catch (_: Throwable) { 0L }
    }

    fun getModelInfo(): String {
        return if (engineHandler != 0L) {
            try { nativeGetModelInfo(engineHandler) } catch (_: Throwable) { "Error" }
        } else "Not initialized"
    }

    fun setContextSize(size: Int) {
        if (engineHandler != 0L && size > 0) {
            try { nativeSetContextSize(engineHandler, size) } catch (_: Throwable) {}
        }
    }

    fun setGPULayers(layers: Int) {
        if (engineHandler != 0L && layers >= 0) {
            try { nativeSetGPULayers(engineHandler, layers) } catch (_: Throwable) {}
        }
    }

    private fun ensureInitialized(): Boolean {
        if (!isInitialized) initialize()
        return isInitialized
    }
}
