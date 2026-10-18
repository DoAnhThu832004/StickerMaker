# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# ==========================================
# 1. ROOM DATABASE (Giữ lại tên cột và Entity)
# ==========================================
# Không đổi tên các Entity, DAO và TypeConverter để Room map đúng với SQLite
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * {
    @androidx.room.TypeConverter *;
}
-dontwarn androidx.room.paging.**

# ==========================================
# 2. DATA MODELS (Domain & Entity của app)
# ==========================================
-keep class com.thupo.domain.model.** { *; }
-keep class com.thupo.data.local.entity.** { *; }

# ==========================================
# 3. LOTTIE ANIMATION
# ==========================================
# Lottie dùng reflection để khởi tạo các thuộc tính chuyển động
-keep class com.airbnb.lottie.** { *; }

# ==========================================
# 4. COIL (Thư viện tải ảnh)
# ==========================================
-keep class coil.** { *; }
-dontwarn coil.**

# ==========================================
# 5. KOTLIN COROUTINES & FLOW
# ==========================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ==========================================
# 6. GIỮ THÔNG TIN DÒNG ĐỂ ĐỌC CRASH LOG
# ==========================================
-keepattributes SourceFile,LineNumberTable