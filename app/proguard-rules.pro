# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --- Critical Firestore & Data Model Preservation ---
# We keep everything (fields, methods, constructors) for these classes
# because Firestore uses reflection to map data.
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations

-keep class com.harshdeep.jasnify.domain.model.** { *; }
-keep class com.harshdeep.jasnify.data.models.** { *; }
-keep class com.harshdeep.jasnify.data.local.** { *; }

# Specifically keep classes used with Firestore in other packages
-keep class com.harshdeep.jasnify.presentation.viewmodels.ChatSession { *; }
-keep class com.harshdeep.jasnify.presentation.screens.others.AiMessage { *; }

# Keep Firestore PropertyName and DocumentId annotations
-keep @interface com.google.firebase.firestore.PropertyName
-keep @interface com.google.firebase.firestore.DocumentId
-keep @interface com.google.firebase.firestore.IgnoreExtraProperties

# Ensure all annotated members are kept
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName *;
    @com.google.firebase.firestore.DocumentId *;
}

# Preserve Enums (like UserRole)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --- Library Specific Rules ---

# Cloudinary
-keep class com.cloudinary.android.** { *; }
-keep interface com.cloudinary.android.** { *; }

# Gson & Retrofit
-keep class com.google.gson.** { *; }
-keep class com.squareup.retrofit2.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-dontwarn sun.misc.Unsafe

# Suppress warnings for missing Glide and Picasso classes
-dontwarn com.bumptech.glide.**
-dontwarn com.squareup.picasso.**

# Coil
-keep class coil.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }

# BuildConfig
-keep class com.harshdeep.jasnify.BuildConfig { *; }

# Logcat protection
-dontnote com.google.firebase.firestore.util.CustomClassMapper
