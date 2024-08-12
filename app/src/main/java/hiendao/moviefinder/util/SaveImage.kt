package hiendao.moviefinder.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.IOException

fun saveImageToExternalStorage(context: Context, displayName: String, bitmap: Bitmap?): Boolean {
    // Save image to external storage
    if(bitmap == null){
        return false
    }
    val imageCollection = sdk29AndUp {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.WIDTH, bitmap.width)
        put(MediaStore.Images.Media.HEIGHT, bitmap.height)
    }
    return try {
        val contentResolver = context.contentResolver
        contentResolver.insert(imageCollection, contentValues)?.also { uri ->
            contentResolver.openOutputStream(uri).use{ outputStream ->
                outputStream?.let {
                    if(!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)){
                        throw IOException("Couldn't save bitmap")
                    }
                }
            }
        } ?: throw IOException("Couldn't create MediaStore entry")
        true
    }catch (e: IOException){
        e.printStackTrace()
        false
    }

}