package com.thupo.core.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import kotlin.math.max

object SpriteSheetSlicer {

    /**
     * Cắt ảnh Sprite Sheet lớn thành danh sách các ô sticker con.
     * @param sheet Bitmap ảnh lớn do AI sinh ra (thường là 1024x1024)
     * @param rows Số hàng (mặc định 2)
     * @param cols Số cột (mặc định 2)
     * @param insetRatio Tỷ lệ lùi viền (4% mỗi cạnh) để chống Grid Bleeding (cắt phạm tai/tóc hoặc dính vạch kẻ)
     */
    fun sliceGrid(
        sheet: Bitmap,
        rows: Int = 2,
        cols: Int = 2,
        insetRatio: Float = 0.04f
    ): List<Bitmap> {
        val cellWidth = sheet.width / cols
        val cellHeight = sheet.height / rows
        val result = mutableListOf<Bitmap>()

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                // Tọa độ gốc của ô
                val rawX = c * cellWidth
                val rawY = r * cellHeight

                // Áp dụng Inset Margin (lùi vào 4% ở cả 4 phía)
                val insetX = (cellWidth * insetRatio).toInt()
                val insetY = (cellHeight * insetRatio).toInt()

                val cropX = rawX + insetX
                val cropY = rawY + insetY
                val cropWidth = cellWidth - (insetX * 2)
                val cropHeight = cellHeight - (insetY * 2)

                // Đảm bảo không vượt quá biên Bitmap
                val safeX = cropX.coerceIn(0, sheet.width - 1)
                val safeY = cropY.coerceIn(0, sheet.height - 1)
                val safeWidth = cropWidth.coerceAtMost(sheet.width - safeX)
                val safeHeight = cropHeight.coerceAtMost(sheet.height - safeY)

                if (safeWidth > 0 && safeHeight > 0) {
                    val rawCell = Bitmap.createBitmap(sheet, safeX, safeY, safeWidth, safeHeight)
                    // Chuẩn hóa ô về hình vuông (Square Padding) nếu tỷ lệ khác 1:1
                    val squareCell = padToSquare(rawCell)
                    if (squareCell !== rawCell) {
                        rawCell.recycle()
                    }
                    result.add(squareCell)
                }
            }
        }
        return result
    }

    /**
     * Thêm lề trong suốt (Square Padding) để đảm bảo sticker luôn có tỷ lệ 1:1,
     * chống méo mó khi lưới là 2x3.
     */
    fun padToSquare(source: Bitmap): Bitmap {
        if (source.width == source.height) return source

        val size = max(source.width, source.height)
        val squareBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(squareBitmap)
        canvas.drawColor(Color.TRANSPARENT)

        val left = (size - source.width) / 2f
        val top = (size - source.height) / 2f
        canvas.drawBitmap(source, left, top, null)
        return squareBitmap
    }
}
