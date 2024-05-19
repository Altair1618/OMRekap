package com.k2_9.omrekap.utils

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class LiveAnnotationHelper(private val annotatedCameraPreview: AnnotatedCameraPreview): ImageAnalysis.Analyzer {
	override fun analyze(image: ImageProxy) {
		val bitmap = image.toBitmap()
		val processedBitmap = PreprocessHelper.preprocessLiveImage(bitmap)
		annotatedCameraPreview.updateBitmap(processedBitmap)
		image.close()
	}

}
