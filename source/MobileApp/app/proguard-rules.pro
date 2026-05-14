# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in Android SDK/tools/proguard/proguard-android.txt

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Data models (don't obfuscate)
-keep class com.acare.clinic.data.model.** { *; }

# Keep BuildConfig
-keep class com.acare.clinic.BuildConfig { *; }
