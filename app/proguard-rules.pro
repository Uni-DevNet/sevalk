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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Firebase Messaging
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.iid.** { *; }
-dontwarn com.google.firebase.messaging.**

# Firebase Core
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Firebase Firestore
-keepclassmembers class com.google.firebase.firestore.** { *; }
-keep class com.google.firebase.firestore.** { *; }

# Firebase Auth
-keep class com.google.firebase.auth.** { *; }

# Firebase Database
-keep class com.google.firebase.database.** { *; }

# Gson (used for JSON serialization)
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# SevaLK Custom Services
-keep class com.sevalk.services.** { *; }
-keep class com.sevalk.data.repositories.** { *; }
-keep class com.sevalk.data.models.** { *; }

# FCM Service
-keep class com.sevalk.services.SevaLKFirebaseMessagingService { *; }
-keep class com.sevalk.services.FCMNotificationService { *; }
-keep class com.sevalk.services.FCMTokenManager { *; }

# Notification Repository
-keep class com.sevalk.data.repositories.NotificationRepository { *; }
-keep class com.sevalk.data.repositories.NotificationRepositoryImpl { *; }

# Data Models
-keep class com.sevalk.data.models.** { *; }

# Hilt/Dagger
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.AndroidEntryPoint <methods>;
}
-keep @dagger.hilt.InstallIn class *
-keep @dagger.Module class *
-keep @javax.inject.Inject class *
-keepclasseswithmembers class * {
    @javax.inject.Inject <init>(...);
}

# AndroidX and Support Libraries
-keep class androidx.** { *; }
-dontwarn androidx.**

# Coroutines
-keepclassmembernames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepclassmembernames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepclassmembernames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

# OkHttp and networking (if used indirectly)
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Reflection (for annotations and dependency injection)
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

# Parceable
-keep interface android.os.Parcelable
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Timber
-keep class timber.log.** { *; }
-dontwarn timber.log.**

# Email libraries
-keep class com.sun.mail.** { *; }
-keep class javax.mail.** { *; }
-keep class javax.activation.** { *; }
-dontwarn com.sun.mail.**
-dontwarn javax.mail.**
-dontwarn javax.activation.**

# Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Maps
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }

# HTTP URL Connection (for FCM)
-keep class java.net.** { *; }
-keep class java.io.** { *; }

# Compose (if any issues)
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# SLF4J (fixes R8 missing class error)
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.slf4j.**
-keep class org.slf4j.** { *; }