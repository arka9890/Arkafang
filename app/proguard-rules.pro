# app/proguard-rules.pro
# OpenFang mobile - keep Ktor client + kotlinx.serialization
-keepclassmembers class kotlinx.serialization.json.** { *; }
-dontwarn kotlinx.serialization.**
-dontwarn io.ktor.**
-keep class com.openfang.mobile.** { *; }
