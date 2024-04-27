package com.k2_9.omrekap.data.models

import android.graphics.Bitmap
import java.time.Instant

data class ImageSaveData(
	val rawImage: Bitmap,
	var annotatedImage: Bitmap,
	var data: Map<String, Int?>,
	var timestamp: Instant
)
