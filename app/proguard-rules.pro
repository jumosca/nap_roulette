# ─── Attributes ───────────────────────────────────────────────────────────────
# Keep generic signatures and inner class info — required by Hilt, coroutines,
# and Compose compiler. *Annotation* is already kept by the base optimize file.
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# ─── Hilt ─────────────────────────────────────────────────────────────────────
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
-keep @dagger.hilt.android.HiltAndroidApp class *
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keep @dagger.hilt.InstallIn class *
-keep @dagger.Module class *

# ─── Room ─────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keepclassmembers @androidx.room.Dao interface * { *; }

# ─── Kotlin Coroutines ────────────────────────────────────────────────────────
# Dispatcher lookup and exception handler discovery use reflection internally.
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
# R8 must not remove the volatile fields that coroutines use for lock-free atomics.
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ─── AlarmSound sealed class ──────────────────────────────────────────────────
# Subclass names are matched as strings in intent extras (EXTRA_SOUND_TYPE,
# EXTRA_SOUND_RES_NAME) and in `when` expressions. R8 must not rename them.
-keep class com.naproulette.domain.model.AlarmSound { *; }
-keep class com.naproulette.domain.model.AlarmSound$* { *; }
