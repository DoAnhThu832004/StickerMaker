package com.thupo.core.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.min

class StickerImageProcessor(private val context: Context) {

    // Cấu hình Subject Segmenter: bật cờ enableForegroundBitmap để nhận về Bitmap trong suốt
    private val options = SubjectSegmenterOptions.Builder()
        .enableForegroundBitmap()
        .build()

    private val segmenter = SubjectSegmentation.getClient(options)

    /**
     * Bước 1: Load ảnh từ Uri thành Bitmap (đã giới hạn kích thước để không tràn RAM - OOM)
     */
    suspend fun loadBitmapFromUri(uri: Uri, maxDimension: Int = 1024): Bitmap? = withContext(Dispatchers.IO) {
        try {
            // Đọc kích thước trước mà chưa nạp toàn bộ pixel vào RAM (inJustDecodeBounds = true)
            var input: InputStream? = context.contentResolver.openInputStream(uri)
            val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(input, null, boundsOptions)
            input?.close()

            // Tính tỷ lệ thu nhỏ (sampleSize) nếu ảnh chụp từ camera quá lớn (ví dụ 4000x3000)
            var sampleSize = 1
            while (boundsOptions.outWidth / sampleSize > maxDimension || boundsOptions.outHeight / sampleSize > maxDimension) {
                sampleSize *= 2
            }

            // Thực sự đọc Bitmap vào bộ nhớ với tỷ lệ đã tối ưu
            input = context.contentResolver.openInputStream(uri)
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888 // Cần ARGB để hỗ trợ kênh trong suốt
            }
            val bitmap = BitmapFactory.decodeStream(input, null, decodeOptions)
            input?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Bước 2: Dùng ML Kit để tách nền.
     * Chuyển đổi callback của Google Play Services Task sang Kotlin Coroutine (suspend).
     */
    suspend fun removeBackground(sourceBitmap: Bitmap): Bitmap = suspendCancellableCoroutine { continuation ->
        val inputImage = InputImage.fromBitmap(sourceBitmap, 0)

        segmenter.process(inputImage)
            .addOnSuccessListener { result ->
                // foregroundBitmap là ảnh chủ thể đã xóa toàn bộ background thành trong suốt
                val foreground = result.foregroundBitmap
                if (foreground != null) {
                    continuation.resume(foreground)
                } else {
                    // Nếu không nhận diện được chủ thể, trả về ảnh gốc để không bị crash
                    continuation.resume(sourceBitmap)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    /**
     * Bước 3: Tạo viền trắng (White Stroke) bằng Canvas & BlurMaskFilter thuần.
     * @param strokeWidthDp Độ dày của viền (ví dụ: 10f đến 16f)
     */
    fun addStickerOutline(source: Bitmap, strokeWidth: Float = 14f): Bitmap {
        // 1. Tạo viền mở rộng: Thêm padding xung quanh để khi viền nở ra không bị cụt ở mép
        val padding = strokeWidth.toInt() * 2
        val newWidth = source.width + padding * 2
        val newHeight = source.height + padding * 2

        val outputBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)

        // 2. Lấy kênh Alpha của ảnh nguồn
        val alphaMask = source.extractAlpha()

        // 3. Cấu hình Paint để vẽ viền:
        // BlurMaskFilter.Blur.SOLID: Bên trong viền giữ nguyên màu đục, viền ngoài hơi mờ nhẹ để mịn màng
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            maskFilter = BlurMaskFilter(strokeWidth, BlurMaskFilter.Blur.SOLID)
        }

        // 4. Vẽ lớp viền trắng trước (lặp lại 2 lần để viền đặc và trắng tinh)
        canvas.drawBitmap(alphaMask, padding.toFloat(), padding.toFloat(), strokePaint)
        canvas.drawBitmap(alphaMask, padding.toFloat(), padding.toFloat(), strokePaint)

        // 5. Vẽ ảnh gốc đè lên trên lớp viền trắng
        canvas.drawBitmap(source, padding.toFloat(), padding.toFloat(), null)

        return outputBitmap
    }

    /**
     * Bước 4: Chuẩn hóa theo quy chuẩn WhatsApp:
     * - Kích thước: Đúng chuẩn 512x512 pixel (hoặc nằm gọn trong khung 512x512)
     * - Nén sang định dạng WebP không mất chất lượng (Lossless) để giữ nền trong suốt
     */
    fun formatToWhatsAppSticker(bitmap: Bitmap): ByteArray {
        val targetSize = 512
        val scale = min(targetSize.toFloat() / bitmap.width, targetSize.toFloat() / bitmap.height)
        val scaledWidth = (bitmap.width * scale).toInt()
        val scaledHeight = (bitmap.height * scale).toInt()

        // Scale về kích thước tối đa 512
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)

        // Đặt lên một khung vuông 512x512 trong suốt
        val finalSquare = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(finalSquare)
        val left = (targetSize - scaledWidth) / 2f
        val top = (targetSize - scaledHeight) / 2f
        canvas.drawBitmap(resizedBitmap, left, top, null)

        // Nén thành mảng byte WebP
        val outputStream = ByteArrayOutputStream()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            finalSquare.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, outputStream)
        } else {
            @Suppress("DEPRECATION")
            finalSquare.compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
        }
        return outputStream.toByteArray()
    }
}