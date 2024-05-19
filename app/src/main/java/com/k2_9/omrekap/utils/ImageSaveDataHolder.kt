package com.k2_9.omrekap.utils

import android.util.Log
import com.k2_9.omrekap.data.models.ImageSaveData

/**
 * Decorator for ImageSaveData
 */
object ImageSaveDataHolder {
	private var imageSaveData: ImageSaveData? = null

	/**
	 * Set the ImageSaveData
	 * @param data ImageSaveData to be saved
	 */
	fun save(data: ImageSaveData) {
		imageSaveData = data
	}

	/**
	 * Get the ImageSaveData
	 * @return ImageSaveData
	 */
	fun get(): ImageSaveData {
		if (imageSaveData == null) {
			Log.e("ImageSaveDataHolder", "ImageSaveData is null")
			throw RuntimeException("ImageSaveData is null")
		}

		return imageSaveData!!
	}
}
