package com.mobileaistudio.inference

object LlamaCppEngine {

    init {
        System.loadLibrary("llama_android")
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

    // --- Singleton instance ---
    private var engineHandler: Long = 0L
    private var isInitialized = false

    fun initialize(): Boolean {
        if (isInitialized) return true
        return try {
            engineHandler = nativeInitEngine()
            isInitialized = engineHandler != 0L
            isInitialized
        } catch (e: Exception) {
            false
        }
    }

    fun loadModel(modelPath: String): Boolean {
        if (!ensureInitialized()) return false
        return try {
            nativeLoadModel(engineHandler, modelPath)
        } catch (e: Exception) {
            false
        }
    }

    fun unloadModel() {
        if (engineHandler != 0L) {
            nativeUnloadModel(engineHandler)
        }
    }

    fun isModelLoaded(): Boolean {
        return if (engineHandler != 0L) nativeIsModelLoaded(engineHandler) else false
    }

    fun release() {
        if (engineHandler != 0L) {
            nativeReleaseEngine(engineHandler)
            engineHandler = 0L
            isInitialized = false
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
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun getTokenCount(text: String): Int {
        return if (engineHandler != 0L) nativeGetTokenCount(engineHandler, text) else 0
    }

    fun getAvailableRAM(): Long {
        return try { nativeGetAvailableRAM() } catch (_: Exception) { 0L }
    }

    fun getAvailableVRAM(): Long {
        return try { nativeGetAvailableVRAM() } catch (_: Exception) { 0L }
    }

    fun getModelInfo(): String {
        return if (engineHandler != 0L) nativeGetModelInfo(engineHandler) else "Not initialized"
    }

    fun setContextSize(size: Int) {
        if (engineHandler != 0L) nativeSetContextSize(engineHandler, size)
    }

    fun setGPULayers(layers: Int) {
        if (engineHandler != 0L) nativeSetGPULayers(engineHandler, layers)
    }

    private fun ensureInitialized(): Boolean {
        if (!isInitialized) initialize()
        return isInitialized
    }
}
