package com.fsacchi.ui.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val NAME_SHARE_IMAGE_TEMP = "images/weather_share.png"
const val PNG_TYPE_MIME = "image/png"
const val QUALITY_IMAGE = 90

suspend fun Activity.captureScreenBitmap(width: Int, height: Int): Bitmap? {
    return suspendCoroutine { continuation ->
        val bitmap = createBitmap(width, height)
        PixelCopy.request(
            window,
            bitmap,
            { result -> continuation.resume(if (result == PixelCopy.SUCCESS) bitmap else null) },
            Handler(Looper.getMainLooper())
        )
    }
}

suspend fun Activity.shareScreenshot(width: Int, height: Int, shareText: String, chooserTitle: String) {
    val bitmap = captureScreenBitmap(width, height) ?: return
    val file = withContext(Dispatchers.IO) {
        File(cacheDir, NAME_SHARE_IMAGE_TEMP).also { f ->
            f.parentFile?.mkdirs()
            FileOutputStream(f).use { bitmap.compress(Bitmap.CompressFormat.PNG, QUALITY_IMAGE, it) }
        }
    }
    val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = PNG_TYPE_MIME
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, shareText)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(intent, chooserTitle))
}
