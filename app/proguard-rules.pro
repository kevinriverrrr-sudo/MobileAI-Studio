# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keep class com.mobileaistudio.data.remote.huggingface.dto.** { *; }
-keep class com.mobileaistudio.data.local.db.entities.** { *; }
-keep class com.mobileaistudio.inference.** { *; }
