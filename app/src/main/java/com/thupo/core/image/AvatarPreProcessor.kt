package com.thupo.core.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.media.ExifInterface
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.math.max
import kotlin.math.min

@Singleton
class AvatarPreProcessor @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()
    )

    /**
     * Tiền xử lý ảnh selfie: Sửa xoay EXIF -> Tìm mặt to nhất -> Crop bất đối xứng lấy tóc & vai -> Scale 512x512
     */
    suspend fun processAndCrop(imageUri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            // 1. Đọc và xoay ảnh theo thẻ EXIF
            val rotatedBitmap = readRotatedBitmap(imageUri) ?: return@withContext null

            // 2. Nhận diện khuôn mặt lớn nhất
            val largestFace = detectLargestFace(rotatedBitmap)

            val croppedBitmap = if (largestFace != null) {
                // 3A. Có mặt người -> Crop bất đối xứng (55% trên lấy tóc, 75% dưới lấy vai áo)
                cropAsymmetric(rotatedBitmap, largestFace)
            } else {
                // 3B. Thú cưng hoặc vật thể (Pet Fallback) -> Center Crop hình vuông
                cropCenterSquare(rotatedBitmap)
            }

            if (croppedBitmap !== rotatedBitmap) {
                rotatedBitmap.recycle()
            }

            // 4. Scale về kích thước chuẩn 512x512 (dung lượng siêu nhẹ ~150KB)
            scaleToStandard(croppedBitmap, 512)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Đọc ảnh kèm xử lý góc xoay EXIF (sửa lỗi camera trước bị lộn ngang 90 độ)
     */
    private fun readRotatedBitmap(uri: Uri): Bitmap? {
        var input: InputStream? = null
        return try {
            input = context.contentResolver.openInputStream(uri) ?: return null
            val exif = ExifInterface(input)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            input.close()

            // Đọc Bitmap thực tế
            input = context.contentResolver.openInputStream(uri) ?: return null
            val original = BitmapFactory.decodeStream(input) ?: return null
            input.close()

            val rotationDegrees = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }

            if (rotationDegrees != 0f) {
                val matrix = Matrix().apply { postRotate(rotationDegrees) }
                val rotated = Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
                original.recycle()
                rotated
            } else {
                original
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            input?.close()
        }
    }

    /**
     * Tìm khuôn mặt có diện tích lớn nhất trong ảnh (xử lý trường hợp group selfie)
     */
    private suspend fun detectLargestFace(bitmap: Bitmap): Rect? =
        suspendCancellableCoroutine { cont ->
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            faceDetector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        // Chọn khuôn mặt có diện tích lớn nhất (width * height)
                        val primary = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }
                        cont.resume(primary?.boundingBox)
                    } else {
                        cont.resume(null)
                    }
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }

    /**
     * Thuật toán Crop bất đối xứng (Asymmetric Padding):
     * Mở rộng lên trên 55% để lấy tóc/mũ, mở rộng xuống dưới 75% để lấy cổ/áo
     */
    private fun cropAsymmetric(source: Bitmap, faceRect: Rect): Bitmap {
        val faceW = faceRect.width().toFloat()
        val faceH = faceRect.height().toFloat()

        val topPad = (faceH * 0.55f).toInt()
        val bottomPad = (faceH * 0.75f).toInt()
        val horizPad = (faceW * 0.50f).toInt()

        var left = faceRect.left - horizPad
        var top = faceRect.top - topPad
        var right = faceRect.right + horizPad
        var bottom = faceRect.bottom + bottomPad

        // Tính chiều rộng và chiều cao bao quanh
        var cropW = right - left
        var cropH = bottom - top

        // Ép về hình vuông bằng cách mở rộng chiều ngắn hơn
        val squareDim = max(cropW, cropH)
        val diffW = squareDim - cropW
        val diffH = squareDim - cropH

        left -= diffW / 2
        right += diffW - (diffW / 2)
        top -= diffH / 2
        bottom += diffH - (diffH / 2)

        // Giới hạn trong kích thước thật của ảnh (Clamp bounds)
        val safeLeft = left.coerceAtLeast(0)
        val safeTop = top.coerceAtLeast(0)
        val safeRight = right.coerceAtMost(source.width)
        val safeBottom = bottom.coerceAtMost(source.height)

        val finalW = safeRight - safeLeft
        val finalH = safeBottom - safeTop

        return if (finalW > 0 && finalH > 0) {
            Bitmap.createBitmap(source, safeLeft, safeTop, finalW, finalH)
        } else {
            cropCenterSquare(source)
        }
    }

    /**
     * Fallback cho thú cưng hoặc vật thể: Cắt khung vuông chính giữa ảnh
     */
    private fun cropCenterSquare(source: Bitmap): Bitmap {
        val size = min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        return Bitmap.createBitmap(source, x, y, size, size)
    }

    /**
     * Scale ảnh về kích thước tối đa (512x512)
     */
    private fun scaleToStandard(source: Bitmap, maxDim: Int): Bitmap {
        if (source.width <= maxDim && source.height <= maxDim) return source
        val scale = min(maxDim.toFloat() / source.width, maxDim.toFloat() / source.height)
        val targetW = (source.width * scale).toInt()
        val targetH = (source.height * scale).toInt()
        val scaled = Bitmap.createScaledBitmap(source, targetW, targetH, true)
        if (scaled !== source) {
            source.recycle()
        }
        return scaled
    }
}
