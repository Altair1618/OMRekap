package com.k2_9.omrekap.data.configs.omr

/**
 * Configuration for the OMR helper
 * @param omrCropper cropper for the OMR section
 */
open class OMRHelperConfig(
	val omrCropper: OMRCropper,
	val columnCount: Int,
) {
	init {
		require(columnCount >= 1) { "contour detection must have at least 1 column" }
	}
}
