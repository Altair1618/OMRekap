package com.k2_9.omrekap.data.configs.omr

import org.opencv.core.Mat

class TemplateMatchingOMRDetectorConfig(
	omrCropper: OMRCropper,
	template: Mat,
	similarityThreshold: Float
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

	    this.template = template.clone()
		this.similarityThreshold = similarityThreshold
	}
}
