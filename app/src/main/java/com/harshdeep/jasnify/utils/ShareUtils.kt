package com.harshdeep.jasnify.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareUtils {
    fun shareImage(context: Context, bitmap: Bitmap, whatsappOnly: Boolean = false) {
        try {
            val cachePath = File(context.cacheDir, "shared_images")
            cachePath.mkdirs()
            val imageFile = File(cachePath, "invitation_card.png")
            val stream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
                if (whatsappOnly) {
                    setPackage("com.whatsapp")
                }
            }
            
            val chooser = Intent.createChooser(shareIntent, "Share Card")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(if (whatsappOnly) shareIntent else chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
