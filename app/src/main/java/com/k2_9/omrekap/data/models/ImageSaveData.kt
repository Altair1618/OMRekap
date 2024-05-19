package com.k2_9.omrekap.data.models

import android.graphics.Bitmap
import java.time.Instant

/**
 * Data class for saving image data
 * @param rawImage raw image
 * @param annotatedImage image with annotations
 * @param data map of candidate names and their vote counts
 * @param timestamp timestamp of the image
 */
data class ImageSaveData(
	val rawImage: Bitmap,
	var annotatedImage: Bitmap,
	var data: Map<String, Int?>,
	var timestamp: Instant,
)
