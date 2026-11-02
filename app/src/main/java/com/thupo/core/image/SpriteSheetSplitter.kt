package com.thupo.core.image

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * BoundingBox biểu diễn vùng bao quanh một sticker trên ảnh Sprite Sheet.
 */
data class BoundingBox(
    val minX: Int,
    val minY: Int,
    val maxX: Int,
    val maxY: Int
) {
    val width: Int get() = (maxX - minX + 1).coerceAtLeast(0)
    val height: Int get() = (maxY - minY + 1).coerceAtLeast(0)
    val area: Int get() = width * height
    val centerX: Float get() = (minX + maxX) / 2f
    val centerY: Float get() = (minY + maxY) / 2f

    /**
     * Gộp với một BoundingBox khác để bao phủ toàn bộ cả hai vùng.
     */
    fun merge(other: BoundingBox): BoundingBox {
        return BoundingBox(
            minX = min(this.minX, other.minX),
            minY = min(this.minY, other.minY),
            maxX = max(this.maxX, other.maxX),
            maxY = max(this.maxY, other.maxY)
        )
    }

    /**
     * Tính khoảng cách ngắn nhất (Euclidean distance) giữa 2 BoundingBox trong không gian 2D.
     * Nếu 2 box chạm nhau hoặc lồng nhau thì khoảng cách bằng 0.
     */
    fun distanceTo(other: BoundingBox): Float {
        val dx = max(0, max(this.minX - other.maxX, other.minX - this.maxX))
        val dy = max(0, max(this.minY - other.maxY, other.minY - this.maxY))
        return hypot(dx.toFloat(), dy.toFloat())
    }

    /**
     * Mở rộng BoundingBox thêm một lượng padding xung quanh, có clamp an toàn trong biên ảnh.
     */
    fun expand(padding: Int, maxWidth: Int, maxHeight: Int): BoundingBox {
        return BoundingBox(
            minX = (minX - padding).coerceIn(0, maxWidth - 1),
            minY = (minY - padding).coerceIn(0, maxHeight - 1),
            maxX = (maxX + padding).coerceIn(0, maxWidth - 1),
            maxY = (maxY + padding).coerceIn(0, maxHeight - 1)
        )
    }
}

/**
 * Biểu diễn một Connected Component (vùng pixel tiền cảnh liên thông) tìm được trên ảnh.
 */
data class Component(
    val id: Int,
    val pixelCount: Int,
    val bbox: BoundingBox,
    val cx: Float,
    val cy: Float
)

/**
 * Bộ xử lý cắt và tách Sticker từ ảnh Sprite Sheet (On-Device Processing):
 * - Không dùng OpenCV để giữ APK siêu nhẹ.
 * - Thao tác trực tiếp trên mảng IntArray từ Bitmap.getPixels().
 * - Thuật toán BFS dùng hàng đợi IntArray phẳng với 2 con trỏ head/tail (tuyệt đối không dùng đệ quy hoặc autoboxing).
 * - Chạy bất đồng bộ an toàn trên Dispatchers.Default.
 */
class SpriteSheetSplitter(
    private val processor: StickerImageProcessor? = null
) {

    /**
     * PIPELINE ĐẦU-CUỐI:
     * 1. Xóa nền trắng thông minh từ rìa ảnh (removeBackground).
     * 2. Tìm Connected Components, gom cụm Anchor & Mảnh rời, Fallback an toàn (extractStickers).
     * 3. Cắt từng sticker & đưa qua StickerImageProcessor hậu kỳ (cropAndProcess).
     */
    suspend fun splitSpriteSheet(
        sheetBitmap: Bitmap,
        rows: Int = 2,
        cols: Int = 2,
        padding: Int = 8
    ): List<Bitmap> = withContext(Dispatchers.Default) {
        // Bước 1: Bóc nền trắng phẳng
        val transparentSheet = removeBackground(sheetBitmap)

        // Bước 2: Phân tích & tìm BoundingBox tối ưu cho từng sticker
        val boxes = extractStickers(transparentSheet, rows, cols)

        // Bước 3: Cắt và hậu kỳ viền trắng 512x512
        val results = cropAndProcess(transparentSheet, boxes, padding)

        // Giải phóng bitmap trung gian ngay lập tức
        if (transparentSheet !== sheetBitmap) {
            transparentSheet.recycle()
        }

        results
    }

    /**
     * BƯỚC 1: Xóa nền trắng thông minh từ rìa ảnh (removeBackground)
     * - BFS Flood Fill xuất phát từ 4 cạnh viền (tolerance = 24).
     * - Xóa vùng nền kín (Enclosed Background) nếu đồng nhất trắng tuyệt đối (sai số <= 5 với pixel góc 0,0).
     * - Khử răng cưa 1px viền (Feathering) cho các pixel tiền cảnh tiếp giáp nền có độ sáng cao (luminance > 200).
     */
    suspend fun removeBackground(
        bitmap: Bitmap,
        tol: Int = 24
    ): Bitmap = withContext(Dispatchers.Default) {
        val width = bitmap.width
        val height = bitmap.height
        val totalPixels = width * height

        val pixels = IntArray(totalPixels)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // Lấy màu mẫu từ góc viền (0, 0)
        val refColor = pixels[0]
        val r0 = (refColor shr 16) and 0xFF
        val g0 = (refColor shr 8) and 0xFF
        val b0 = refColor and 0xFF

        fun isNearWhite(color: Int, tolerance: Int): Boolean {
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = color and 0xFF
            val threshold = 255 - tolerance
            return r >= threshold && g >= threshold && b >= threshold
        }

        fun isUltraWhite(color: Int): Boolean {
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = color and 0xFF
            return abs(r - r0) <= 5 && abs(g - g0) <= 5 && abs(b - b0) <= 5
        }

        val isBg = BooleanArray(totalPixels)
        val queue = IntArray(totalPixels)
        var head = 0
        var tail = 0

        // 1. Nạp tất cả pixel viền ngoài (4 cạnh mép ảnh) thỏa điều kiện trắng vào hàng đợi BFS
        for (x in 0 until width) {
            val topIdx = x
            if (!isBg[topIdx] && isNearWhite(pixels[topIdx], tol)) {
                isBg[topIdx] = true
                queue[tail++] = topIdx
            }
            val botIdx = (height - 1) * width + x
            if (!isBg[botIdx] && isNearWhite(pixels[botIdx], tol)) {
                isBg[botIdx] = true
                queue[tail++] = botIdx
            }
        }
        for (y in 0 until height) {
            val leftIdx = y * width
            if (!isBg[leftIdx] && isNearWhite(pixels[leftIdx], tol)) {
                isBg[leftIdx] = true
                queue[tail++] = leftIdx
            }
            val rightIdx = y * width + (width - 1)
            if (!isBg[rightIdx] && isNearWhite(pixels[rightIdx], tol)) {
                isBg[rightIdx] = true
                queue[tail++] = rightIdx
            }
        }

        // Lan truyền BFS Flood Fill 4 hướng từ biên ảnh vào trong
        while (head < tail) {
            val curr = queue[head++]
            val cx = curr % width
            val cy = curr / width

            // Up
            if (cy > 0) {
                val up = curr - width
                if (!isBg[up] && isNearWhite(pixels[up], tol)) {
                    isBg[up] = true
                    queue[tail++] = up
                }
            }
            // Down
            if (cy < height - 1) {
                val down = curr + width
                if (!isBg[down] && isNearWhite(pixels[down], tol)) {
                    isBg[down] = true
                    queue[tail++] = down
                }
            }
            // Left
            if (cx > 0) {
                val left = curr - 1
                if (!isBg[left] && isNearWhite(pixels[left], tol)) {
                    isBg[left] = true
                    queue[tail++] = left
                }
            }
            // Right
            if (cx < width - 1) {
                val right = curr + 1
                if (!isBg[right] && isNearWhite(pixels[right], tol)) {
                    isBg[right] = true
                    queue[tail++] = right
                }
            }
        }

        // 2. Xử lý vùng nền kín (Enclosed Background)
        // Duyệt các vùng trắng kín (như khoảng trống giữa tay và hông) chưa nối với viền
        val visitedEnclosed = BooleanArray(totalPixels)
        for (i in 0 until totalPixels) {
            if (!isBg[i] && !visitedEnclosed[i] && isUltraWhite(pixels[i])) {
                var encHead = 0
                var encTail = 0
                visitedEnclosed[i] = true
                queue[encTail++] = i
                var isTrulyEnclosedBackground = true

                while (encHead < encTail) {
                    val curr = queue[encHead++]
                    val cx = curr % width
                    val cy = curr / width

                    // Kiểm tra 4 lân cận
                    val up = if (cy > 0) curr - width else -1
                    val down = if (cy < height - 1) curr + width else -1
                    val left = if (cx > 0) curr - 1 else -1
                    val right = if (cx < width - 1) curr + 1 else -1

                    val neighbors = intArrayOf(up, down, left, right)
                    for (n in neighbors) {
                        if (n == -1 || isBg[n]) continue
                        if (!visitedEnclosed[n]) {
                            if (isUltraWhite(pixels[n])) {
                                visitedEnclosed[n] = true
                                queue[encTail++] = n
                            } else {
                                // Nếu vùng này tiếp xúc với pixel trắng bóng mờ (near-white nhưng không ultra-white),
                                // chứng tỏ đây là trang phục trắng / bụng trắng có đổ bóng, không phải nền trống.
                                if (isNearWhite(pixels[n], tol)) {
                                    isTrulyEnclosedBackground = false
                                }
                            }
                        }
                    }
                }

                if (isTrulyEnclosedBackground) {
                    for (k in 0 until encTail) {
                        isBg[queue[k]] = true
                    }
                }
            }
        }

        // 3. Khử răng cưa viền (1px Feathering) & xóa nền thành Alpha 0
        val outPixels = IntArray(totalPixels)
        for (idx in 0 until totalPixels) {
            if (isBg[idx]) {
                outPixels[idx] = 0 // Trong suốt hoàn toàn
            } else {
                val color = pixels[idx]
                val r = (color shr 16) and 0xFF
                val g = (color shr 8) and 0xFF
                val b = color and 0xFF
                val luminance = (299 * r + 587 * g + 114 * b) / 1000

                val cx = idx % width
                val cy = idx / width
                val touchesBg = (cy > 0 && isBg[idx - width]) ||
                                (cy < height - 1 && isBg[idx + width]) ||
                                (cx > 0 && isBg[idx - 1]) ||
                                (cx < width - 1 && isBg[idx + 1])

                if (touchesBg && luminance > 200) {
                    // Giảm alpha xuống 0x80 (128) để viền tiếp giáp mềm mượt
                    outPixels[idx] = (0x80 shl 24) or (color and 0x00FFFFFF)
                } else {
                    outPixels[idx] = color
                }
            }
        }

        Bitmap.createBitmap(outPixels, width, height, Bitmap.Config.ARGB_8888)
    }

    /**
     * BƯỚC 2: Tìm Connected Components & Gom cụm thông minh (extractStickers)
     * 1. Tìm tất cả các cụm pixel liên thông (alpha > 0) bằng BFS 8 hướng (tránh đứt nét vẽ mảnh).
     * 2. Lọc nhiễu: Loại bỏ component < 0.05% tổng diện tích ảnh.
     * 3. Tìm Core Anchor cho từng ô lưới lý thuyết (rows x cols) và gom các mảnh rời vào Anchor gần nhất.
     * 4. Fallback an toàn về tọa độ lưới nếu có ô trống hoặc AI vẽ liền khối.
     */
    suspend fun extractStickers(
        alphaBitmap: Bitmap,
        rows: Int,
        cols: Int
    ): List<BoundingBox> = withContext(Dispatchers.Default) {
        val width = alphaBitmap.width
        val height = alphaBitmap.height
        val totalPixels = width * height

        val pixels = IntArray(totalPixels)
        alphaBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val visited = BooleanArray(totalPixels)
        val queue = IntArray(totalPixels)
        val components = mutableListOf<Component>()

        // 1. BFS 8 hướng tìm tất cả Connected Components
        for (i in 0 until totalPixels) {
            val alpha = (pixels[i] ushr 24) and 0xFF
            if (alpha > 0 && !visited[i]) {
                var head = 0
                var tail = 0
                visited[i] = true
                queue[tail++] = i

                var minX = i % width
                var maxX = minX
                var minY = i / width
                var maxY = minY
                var sumX = 0L
                var sumY = 0L
                var pixelCount = 0

                while (head < tail) {
                    val curr = queue[head++]
                    val cx = curr % width
                    val cy = curr / width

                    pixelCount++
                    sumX += cx
                    sumY += cy

                    if (cx < minX) minX = cx
                    if (cx > maxX) maxX = cx
                    if (cy < minY) minY = cy
                    if (cy > maxY) maxY = cy

                    // Duyệt 8 hướng xung quanh
                    val yStart = max(0, cy - 1)
                    val yEnd = min(height - 1, cy + 1)
                    val xStart = max(0, cx - 1)
                    val xEnd = min(width - 1, cx + 1)

                    for (ny in yStart..yEnd) {
                        for (nx in xStart..xEnd) {
                            if (nx == cx && ny == cy) continue
                            val nIdx = ny * width + nx
                            val nAlpha = (pixels[nIdx] ushr 24) and 0xFF
                            if (nAlpha > 0 && !visited[nIdx]) {
                                visited[nIdx] = true
                                queue[tail++] = nIdx
                            }
                        }
                    }
                }

                val bbox = BoundingBox(minX, minY, maxX, maxY)
                val compCx = sumX.toFloat() / pixelCount
                val compCy = sumY.toFloat() / pixelCount
                components.add(Component(components.size, pixelCount, bbox, compCx, compCy))
            }
        }

        // 2. Lọc nhiễu: Loại bỏ các component < 0.05% diện tích ảnh
        val minArea = (totalPixels * 0.0005f).toInt()
        val validComponents = components.filter { it.pixelCount >= minArea }

        // 3. Xác định Core Anchors cho từng ô lưới
        val cellWidth = width / cols
        val cellHeight = height / rows
        val totalCells = rows * cols

        val anchorBoxes = arrayOfNulls<BoundingBox>(totalCells)
        val anchorIds = mutableSetOf<Int>()

        // Phân loại component theo ô lưới dựa trên tâm (cx, cy)
        val cellsComponents = Array(totalCells) { mutableListOf<Component>() }
        for (comp in validComponents) {
            val c = (comp.cx / cellWidth).toInt().coerceIn(0, cols - 1)
            val r = (comp.cy / cellHeight).toInt().coerceIn(0, rows - 1)
            val cellIdx = r * cols + c
            cellsComponents[cellIdx].add(comp)
        }

        // Chọn component lớn nhất trong mỗi ô làm Anchor
        for (cellIdx in 0 until totalCells) {
            val inCell = cellsComponents[cellIdx]
            if (inCell.isNotEmpty()) {
                val anchor = inCell.maxByOrNull { it.pixelCount }!!
                anchorBoxes[cellIdx] = anchor.bbox
                anchorIds.add(anchor.id)
            }
        }

        // Gom mảnh rời (component phụ) vào Anchor gần nhất
        val remainingComponents = validComponents.filter { it.id !in anchorIds }
        for (comp in remainingComponents) {
            var closestIdx = -1
            var minDistance = Float.MAX_VALUE

            for (cellIdx in 0 until totalCells) {
                val currentBox = anchorBoxes[cellIdx] ?: continue
                val dist = currentBox.distanceTo(comp.bbox)
                if (dist < minDistance) {
                    minDistance = dist
                    closestIdx = cellIdx
                }
            }

            if (closestIdx != -1) {
                anchorBoxes[closestIdx] = anchorBoxes[closestIdx]!!.merge(comp.bbox)
            }
        }

        // 4. Fallback an toàn: Với các ô không tìm được Anchor, fallback về chia đều theo tọa độ lưới
        val result = mutableListOf<BoundingBox>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cellIdx = r * cols + c
                val box = anchorBoxes[cellIdx]
                if (box != null && box.area > 0) {
                    result.add(box)
                } else {
                    val fallbackBox = BoundingBox(
                        minX = c * cellWidth,
                        minY = r * cellHeight,
                        maxX = ((c + 1) * cellWidth - 1).coerceAtMost(width - 1),
                        maxY = ((r + 1) * cellHeight - 1).coerceAtMost(height - 1)
                    )
                    result.add(fallbackBox)
                }
            }
        }

        result
    }

    /**
     * BƯỚC 3: Cắt ảnh & Hậu kỳ (cropAndProcess)
     * - Cắt từng BoundingBox có cộng thêm padding an toàn (mặc định 8px).
     * - Đưa từng sticker qua StickerImageProcessor.process() để bo viền trắng và chuẩn hóa 512x512.
     */
    suspend fun cropAndProcess(
        sourceBitmap: Bitmap,
        boxes: List<BoundingBox>,
        padding: Int = 8
    ): List<Bitmap> = withContext(Dispatchers.Default) {
        val resultBitmaps = mutableListOf<Bitmap>()

        for (box in boxes) {
            val expanded = box.expand(padding, sourceBitmap.width, sourceBitmap.height)
            if (expanded.width <= 0 || expanded.height <= 0) continue

            val cropped = Bitmap.createBitmap(
                sourceBitmap,
                expanded.minX,
                expanded.minY,
                expanded.width,
                expanded.height
            )

            val processed = if (processor != null) {
                val finalSticker = processor.process(cropped)
                if (finalSticker !== cropped) {
                    cropped.recycle()
                }
                finalSticker
            } else {
                cropped
            }

            resultBitmaps.add(processed)
        }

        resultBitmaps
    }
}
