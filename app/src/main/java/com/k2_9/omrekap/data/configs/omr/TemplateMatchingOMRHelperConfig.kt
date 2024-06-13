package com.k2_9.omrekap.data.configs.omr

import org.opencv.core.Mat

/**
 * Configuration for the OMR helper using template matching
 * @param omrCropper cropper for the OMR section
 * @param templateLoader loader for the template image
 * @param similarityThreshold threshold for the similarity between the template and the cropped image
 */
class TemplateMatchingOMRHelperConfig(
	omrCropper: OMRCropper,
	columnCount: Int,
	templateLoader: CircleTemplateLoader?,
	similarityThreshold: Float,
) : OMRHelperConfig(omrCropper, columnCount) {
	var template: Mat?
		private set
		get() = field?.clone()

	var similarityThreshold: Float
		private set

	init {
		require(similarityThreshold in 0.0..1.0) {
			"similarity_threshold must be between 0 and 1"
		}

		this.template = templateLoader?.loadTemplateImage()
		this.similarityThreshold = similarityThreshold
	}

	public fun setTemplate(templateLoader: CircleTemplateLoader) {
		this.template = templateLoader.loadTemplateImage()
	}
}
