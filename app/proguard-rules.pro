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

# ============================================================================
# REGLAS ESPECÍFICAS PARA CLEAN ARCHITECTURE - FSA APP
# ============================================================================

# Mantener todas las entidades del dominio (Data Classes)
-keep class com.molytech.fsa.domain.entities.** { *; }

# Mantener todas las interfaces de repositorios
-keep interface com.molytech.fsa.domain.repositories.** { *; }

# Mantener todos los Use Cases
-keep class com.molytech.fsa.domain.usecases.** { *; }

# Mantener ViewModels y sus métodos públicos
-keep class com.molytech.fsa.ui.**.ViewModel { *; }
-keep class com.molytech.fsa.ui.**.ViewModelFactory { *; }

# Mantener Activities y sus métodos de lifecycle
-keep class com.molytech.fsa.ui.**.Activity { *; }

# ============================================================================
# REGLAS PARA FIREBASE
# ============================================================================

# Firebase Auth
-keep class com.google.firebase.auth.** { *; }
-keepclassmembers class com.google.firebase.auth.** { *; }

# Firebase Firestore
-keep class com.google.firebase.firestore.** { *; }
-keepclassmembers class com.google.firebase.firestore.** { *; }

# Firebase Analytics
-keep class com.google.firebase.analytics.** { *; }
-keepclassmembers class com.google.firebase.analytics.** { *; }

# Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ============================================================================
# REGLAS PARA KOTLIN Y CORRUTINAS
# ============================================================================

# Kotlin Coroutines
-keepclassmembernames class kotlinx.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Kotlin Reflection
-keep class kotlin.reflect.** { *; }
-keep class kotlin.Metadata { *; }

# ============================================================================
# REGLAS PARA ANDROID COMPONENTS
# ============================================================================

# ViewBinding y DataBinding
-keep class * implements androidx.viewbinding.ViewBinding { *; }
-keep class androidx.databinding.** { *; }

# LiveData y ViewModel
-keep class androidx.lifecycle.** { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Navigation Component
-keep class androidx.navigation.** { *; }

# ============================================================================
# REGLAS PARA BIBLIOTECAS ESPECÍFICAS
# ============================================================================

# OSMDroid (Mapas)
-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**

# Cloudinary
-keep class com.cloudinary.** { *; }
-dontwarn com.cloudinary.**

# Glide (Carga de imágenes)
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
    *** rewind();
}

# ============================================================================
# REGLAS GENERALES DE SEGURIDAD
# ============================================================================

# Mantener atributos importantes
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Evitar warnings de bibliotecas
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# Serialización
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================================================================
# REGLAS PARA DEBUGGING Y CRASH REPORTING
# ============================================================================

# Mantener información para stack traces útiles
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Mantener información de símbolos para Google Play Console
-printmapping mapping.txt
-printusage usage.txt
-printseeds seeds.txt

# Evitar ofuscar clases que aparecen en logs de crash
-keep class com.molytech.fsa.** {
    public protected *;
}

# Mantener información de método y línea para stack traces
-keepattributes SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,Signature,Exceptions
