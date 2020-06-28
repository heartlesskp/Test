package com.expertbrains.startegictest.widgets

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object Utility {
    fun getEncodeBitmapString(selectedBitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val byte: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byte, Base64.DEFAULT)
    }

    fun getDecodeStringBitmap(bitmapString: String): Bitmap {
        val decodedString: ByteArray =
            Base64.decode(bitmapString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}