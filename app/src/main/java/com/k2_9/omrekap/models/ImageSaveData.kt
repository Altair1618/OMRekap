package com.k2_9.omrekap.models

import android.graphics.Bitmap

data class ImageSaveData(
	val rawImage: Bitmap,
	var annotatedImage: Bitmap,
	var data: Map<String, Int>?,
)
