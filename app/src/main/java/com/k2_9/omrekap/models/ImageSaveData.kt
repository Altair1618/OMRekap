package com.k2_9.omrekap.models

import android.net.Uri

data class ImageSaveData(val rawImage: Uri, var annotatedImage: Uri?, var data: Map<String, Int>?) {
	fun isProcessed(): Boolean {
		return annotatedImage != null && data != null
	}
}
