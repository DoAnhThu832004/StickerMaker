package com.thupo.core.whatsapp

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.room.Room
import com.thupo.data.local.AppDatabase
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileNotFoundException

class StickerContentProvider : ContentProvider() {

    companion object {
        private const val METADATA = 1
        private const val METADATA_BY_ID = 2
        private const val STICKERS = 3
        private const val STICKERS_ASSET = 4

        const val STICKER_PACK_IDENTIFIER_IN_QUERY = "sticker_pack_identifier"
        const val STICKER_PACK_NAME_IN_QUERY = "sticker_pack_name"
        const val STICKER_PACK_PUBLISHER_IN_QUERY = "sticker_pack_publisher"
        const val STICKER_PACK_ICON_IN_QUERY = "sticker_pack_icon"
        const val ANDROID_APP_DOWNLOAD_LINK_IN_QUERY = "android_play_store_link"
        const val IOS_APP_DOWNLOAD_LINK_IN_QUERY = "ios_app_download_link"
        const val PUBLISHER_EMAIL = "sticker_pack_publisher_email"
        const val PUBLISHER_WEBSITE = "sticker_pack_publisher_website"
        const val PRIVACY_POLICY_WEBSITE = "sticker_pack_privacy_policy_website"
        const val LICENSE_AGREEMENT_WEBSITE = "sticker_pack_license_agreement_website"
        const val IMAGE_DATA_VERSION = "image_data_version"
        const val AVOID_CACHE = "whatsapp_will_not_cache_stickers"
        const val ANIMATED_STICKER_PACK = "animated_sticker_pack"

        const val STICKER_FILE_NAME_IN_QUERY = "sticker_file_name"
        const val STICKER_FILE_EMOJI_IN_QUERY = "sticker_emoji"

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        private fun initUriMatcher(authority: String) {
            uriMatcher.addURI(authority, "metadata", METADATA)
            uriMatcher.addURI(authority, "metadata/*", METADATA_BY_ID)
            uriMatcher.addURI(authority, "stickers/*", STICKERS)
            uriMatcher.addURI(authority, "stickers_asset/*/*", STICKERS_ASSET)
        }
    }

    private var database: AppDatabase? = null

    override fun onCreate(): Boolean {
        val authority = "${context?.packageName}.stickercontentprovider"
        initUriMatcher(authority)
        return true
    }

    private fun getDatabase(): AppDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context!!.applicationContext,
                AppDatabase::class.java,
                "sticker_maker.db"
            ).build().also { database = it }
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            METADATA -> getPackMetadataCursor(null)
            METADATA_BY_ID -> getPackMetadataCursor(uri.lastPathSegment)
            STICKERS -> getStickersCursor(uri.lastPathSegment)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    private fun getPackMetadataCursor(packIdStr: String?): Cursor {
        val columns = arrayOf(
            STICKER_PACK_IDENTIFIER_IN_QUERY,
            STICKER_PACK_NAME_IN_QUERY,
            STICKER_PACK_PUBLISHER_IN_QUERY,
            STICKER_PACK_ICON_IN_QUERY,
            ANDROID_APP_DOWNLOAD_LINK_IN_QUERY,
            IOS_APP_DOWNLOAD_LINK_IN_QUERY,
            PUBLISHER_EMAIL,
            PUBLISHER_WEBSITE,
            PRIVACY_POLICY_WEBSITE,
            LICENSE_AGREEMENT_WEBSITE,
            IMAGE_DATA_VERSION,
            AVOID_CACHE,
            ANIMATED_STICKER_PACK
        )
        val cursor = MatrixCursor(columns)
        val dao = getDatabase().stickerPackDao()
        val packs = runBlocking {
            if (packIdStr != null) {
                val id = packIdStr.toLongOrNull() ?: return@runBlocking emptyList()
                listOfNotNull(dao.getPackByIdOneShot(id))
            } else {
                emptyList()
            }
        }
        for (pack in packs) {
            val trayIcon = pack.stickers.firstOrNull()?.let { File(it).name } ?: "tray.png"
            cursor.addRow(
                arrayOf(
                    pack.id.toString(),
                    pack.name,
                    "StickerMaker",
                    trayIcon,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "1",
                    0,
                    if (pack.isAnimated) 1 else 0
                )
            )
        }
        return cursor
    }

    private fun getStickersCursor(packIdStr: String?): Cursor {
        val columns = arrayOf(STICKER_FILE_NAME_IN_QUERY, STICKER_FILE_EMOJI_IN_QUERY)
        val cursor = MatrixCursor(columns)
        val id = packIdStr?.toLongOrNull() ?: return cursor
        val dao = getDatabase().stickerPackDao()
        val pack = runBlocking { dao.getPackByIdOneShot(id) } ?: return cursor

        for (stickerUri in pack.stickers) {
            val fileName = File(stickerUri).name
            cursor.addRow(arrayOf(fileName, "☕,🙂"))
        }
        return cursor
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            METADATA -> "vnd.android.cursor.dir/vnd.${context?.packageName}.metadata"
            METADATA_BY_ID -> "vnd.android.cursor.item/vnd.${context?.packageName}.metadata"
            STICKERS -> "vnd.android.cursor.dir/vnd.${context?.packageName}.stickers"
            STICKERS_ASSET -> "image/webp"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val match = uriMatcher.match(uri)
        if (match == STICKERS_ASSET) {
            val segments = uri.pathSegments
            if (segments.size >= 3) {
                val fileName = segments[2]
                val file = File(context?.filesDir, fileName)
                if (file.exists()) {
                    return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                }
            }
        }
        throw FileNotFoundException("File not found for URI: $uri")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
