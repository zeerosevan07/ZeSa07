# ZeSa07 ProGuard rules
-keepattributes *Annotation*
-keep class com.zesa07.security.data.db.entities.** { *; }
-dontwarn okhttp3.**
-dontwarn kotlinx.serialization.**
