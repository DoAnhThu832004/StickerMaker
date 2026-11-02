package com.thupo.core.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class StickerClassifier @Inject constructor() {

    // 1. Cấu hình Face Detector: Bật CLASSIFICATION_MODE_ALL để lấy chỉ số cười (smilingProbability)
    private val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
    private val faceDetector = FaceDetection.getClient(faceDetectorOptions)

    // 2. Cấu hình Image Labeler: Ngưỡng tin cậy tối thiểu 0.5f
    private val labelerOptions = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.5f)
        .build()
    private val imageLabeler = ImageLabeling.getClient(labelerOptions)

    /**
     * Hàm chính: Nhận vào Bitmap sticker -> Trả về 1-3 Emoji phù hợp
     */
    suspend fun classifySticker(bitmap: Bitmap): List<String> = withContext(Dispatchers.Default) {
        // [QUAN TRỌNG]: Giải quyết bẫy kênh Alpha!
        // Vẽ đè ảnh trong suốt lên nền trắng trong RAM để AI không bị nhầm nền đen
        val opaqueBitmap = createOpaqueBitmap(bitmap)
        val inputImage = InputImage.fromBitmap(opaqueBitmap, 0)

        // BƯỚC 1: Chạy Face Detection trước
        val faceEmojis = detectFaceEmotion(inputImage)
        if (faceEmojis != null) {
            return@withContext faceEmojis
        }

        // BƯỚC 2: Nếu không có mặt người, chạy Image Labeling cho động vật / đồ vật
        val labels = detectImageLabels(inputImage)
        EmojiRuleEngine.mapLabelsToEmojis(labels)
    }

    /**
     * Tạo Bitmap nền trắng đặc (Opaque) từ Bitmap trong suốt
     */
    private fun createOpaqueBitmap(source: Bitmap): Bitmap {
        val opaque = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(opaque)
        canvas.drawColor(Color.WHITE) // Nền trắng tinh
        canvas.drawBitmap(source, 0f, 0f, null)
        return opaque
    }

    /**
     * Nhận diện khuôn mặt và cảm xúc nụ cười
     */
    private suspend fun detectFaceEmotion(image: InputImage): List<String>? =
        suspendCancellableCoroutine { cont ->
            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        // Lấy khuôn mặt rõ nhất (thường là mặt đầu tiên)
                        val primaryFace = faces[0]
                        val smileProb = primaryFace.smilingProbability
                        cont.resume(EmojiRuleEngine.mapFaceToEmojis(smileProb))
                    } else {
                        // Không có mặt người trong ảnh
                        cont.resume(null)
                    }
                }
                .addOnFailureListener {
                    // Nếu lỗi Face Detection, tiếp tục fallback sang Labeling
                    cont.resume(null)
                }
        }

    /**
     * Nhận diện các nhãn đồ vật / thú cưng bằng Image Labeling
     */
    private suspend fun detectImageLabels(image: InputImage): List<String> =
        suspendCancellableCoroutine { cont ->
            imageLabeler.process(image)
                .addOnSuccessListener { labels ->
                    val detectedText = labels.map { it.text }
                    cont.resume(detectedText)
                }
                .addOnFailureListener {
                    cont.resume(emptyList())
                }
        }
}