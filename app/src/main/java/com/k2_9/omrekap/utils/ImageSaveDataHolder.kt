package com.k2_9.omrekap.utils

import android.util.Log
import com.k2_9.omrekap.data.models.ImageSaveData

object ImageSaveDataHolder {
	private var imageSaveData: ImageSaveData? = null

	fun save(data: ImageSaveData) {
		imageSaveData = data
	}

	fun get(): ImageSaveData {
		if (imageSaveData == null) {
			Log.e("ImageSaveDataHolder", "ImageSaveData is null")
			throw RuntimeException("ImageSaveData is null")
		}

		return imageSaveData!!
	}
}
