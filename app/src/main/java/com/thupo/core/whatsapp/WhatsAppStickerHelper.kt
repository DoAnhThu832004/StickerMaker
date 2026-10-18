package com.thupo.core.whatsapp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import com.thupo.domain.model.StickerPack

object WhatsAppStickerHelper {
    private const val EXTRA_STICKER_PACK_ID = "sticker_pack_id"
    private const val EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority"
    private const val EXTRA_STICKER_PACK_NAME = "sticker_pack_name"
    private const val ACTION_ENABLE_STICKER_PACK = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"

    const val WHATSAPP_PACKAGE_NAME = "com.whatsapp"
    const val WHATSAPP_BUSINESS_PACKAGE_NAME = "com.whatsapp.w4b"

    fun addToWhatsApp(context: Context, pack: StickerPack) {
        if (!pack.canAddToWhatsApp) {
            Toast.makeText(context, "Cần ít nhất 3 sticker để thêm vào WhatsApp!", Toast.LENGTH_SHORT).show()
            return
        }

        val authority = "${context.packageName}.stickercontentprovider"
        val intent = Intent().apply {
            action = ACTION_ENABLE_STICKER_PACK
            putExtra(EXTRA_STICKER_PACK_ID, pack.id.toString())
            putExtra(EXTRA_STICKER_PACK_AUTHORITY, authority)
            putExtra(EXTRA_STICKER_PACK_NAME, pack.name)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Thiết bị chưa cài đặt WhatsApp!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi khi thêm sticker: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun isWhatsAppInstalled(context: Context): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(WHATSAPP_PACKAGE_NAME, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            try {
                pm.getPackageInfo(WHATSAPP_BUSINESS_PACKAGE_NAME, PackageManager.GET_ACTIVITIES)
                true
            } catch (e2: PackageManager.NameNotFoundException) {
                false
            }
        }
    }
}
