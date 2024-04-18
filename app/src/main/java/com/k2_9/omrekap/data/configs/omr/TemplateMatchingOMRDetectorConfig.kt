package com.k2_9.omrekap.data.configs.omr

import android.content.Context
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.io.InputStream

class TemplateMatchingOMRDetectorConfig(
	omrCropper: OMRCropper,
	templateLoader: CircleTemplateLoader,
	similarityThreshold: Float,
) : OMRDetectorConfig(omrCropper) {
	var template: Mat
		private set
		get() = field.clone()

	var similarityThreshold: Float
		private set
		get() = field

	init {
		require(similarityThreshold in 0.0..1.0) {
			"similarity_threshold must be between 0 and 1"
		}

		this.template = templateLoader.loadTemplateImage()
		this.similarityThreshold = similarityThreshold
	}

	private fun loadTemplateImage(
		appContext: Context,
		resId: Int,
	): Mat {
		val inputStream: InputStream = appContext.resources.openRawResource(resId)
		val byteArray = inputStream.readBytes()
		val imgBuffer = MatOfByte(*byteArray)
		return Imgcodecs.imdecode(imgBuffer, Imgcodecs.IMREAD_GRAYSCALE)
	}
}
