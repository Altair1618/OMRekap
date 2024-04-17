package com.k2_9.omrekap.utils

import android.graphics.Bitmap
import com.k2_9.omrekap.data.models.ImageSaveData

object OMRHelper {
	fun preProcessImage(bitmap: Bitmap): ImageSaveData {
		return ImageSaveData(bitmap, bitmap, mapOf())
	}
}
