package it.polito.mad.buddybench.classes

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.LocaleData
import android.net.Uri
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import it.polito.mad.buddybench.R
import java.io.*
import java.time.LocalDate
import java.time.LocalDateTime


class BitmapUtils(){

    companion object{
        fun uriToBitmap(contentResolver: ContentResolver, selectedFileUri: Uri): Bitmap? {
            try {
                val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
                val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
                val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                parcelFileDescriptor.close()
                return image
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
            return null
        }

        fun toBase64(bitmap: Bitmap): String{
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        fun fromBase64(base64: String): Bitmap?{
            try {
                val decodedString: ByteArray = Base64.decode(base64, Base64.DEFAULT)
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            } catch (e: IOException) {
                e.printStackTrace()

            }
            return null
        }

        fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
            var width = image.width
            var height = image.height
            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 1) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }
            return Bitmap.createScaledBitmap(image, width, height, true)
        }

        fun saveToInternalStorage(applicationContext: Context, bitmapImage: Bitmap, previousPath: Uri? = null): Uri? {

            val profileImageName =  applicationContext.getString(R.string.profileImage).format(LocalDateTime.now())
            val directory: File = applicationContext.filesDir
            val myPath = File(directory, profileImageName)

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(myPath)
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            } finally {
                try {
                    fos!!.close()
                    if(previousPath != null) deleteFile(previousPath.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    return null
                }
            }
            return Uri.parse("${directory.absolutePath}/$profileImageName")
        }

        private fun deleteFile(filePath: String){
            val file = File(filePath)
            try {

                file.delete()



            } catch (e: IOException){
                print("erroreeee")
                e.printStackTrace()
            }
        }
    }

}