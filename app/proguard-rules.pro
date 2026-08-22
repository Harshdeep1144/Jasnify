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

# --- Firestore & Data Models ---
# We keep field names and default constructors for Firestore mapping
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-keep class com.harshdeep.jasnify.domain.model.** {
    <fields>;
    <init>(...);
}
-keep class com.harshdeep.jasnify.data.models.** {
    <fields>;
    <init>(...);
}
-keep class com.harshdeep.jasnify.data.local.** {
    <fields>;
    <init>(...);
}

# Specifically keep classes used with Firestore in other packages
-keep class com.harshdeep.jasnify.presentation.viewmodels.ChatSession {
    <fields>;
    <init>(...);
}
-keep class com.harshdeep.jasnify.presentation.screens.others.AiMessage {
    <fields>;
    <init>(...);
}

# Keep Firestore PropertyName annotations to ensure mapping works correctly
-keep @interface com.google.firebase.firestore.PropertyName
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName *;
}

# --- Library Specific Rules (Targeted to avoid broad keep warnings) ---

# Cloudinary - Keep initialization and essential callbacks
-keep class com.cloudinary.android.MediaManager { *; }
-keep class com.cloudinary.android.callback.UploadCallback { *; }
-keep class com.cloudinary.android.callback.ErrorInfo { *; }

# Gson - Keep serialization annotations and type tokens
-keep class com.google.gson.annotations.SerializedName { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-dontwarn sun.misc.Unsafe

# BuildConfig - Keep for Cloudinary and other services
-keep class com.harshdeep.jasnify.BuildConfig { *; }

# NOTE: Hilt, Coil, and Retrofit typically bundle their own R8/ProGuard rules
# in their AAR files. Explicitly keeping their entire packages is redundant
# and triggers lint warnings.
