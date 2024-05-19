package com.k2_9.omrekap.data.configs.omr

import org.opencv.core.Mat
import org.opencv.core.Rect

/**
 * Cropper for OMR section
 * @param config configuration for the cropper
 */
class OMRCropper(val config: OMRCropperConfig) {
	/**
	 * Crop the image to the section
	 * @param section section to be cropped
	 * @return cropped image
	 */
	fun crop(section: OMRSection): Mat {
		val (x, y) = config.getSectionPosition(section)
		val (width, height) = config.omrSectionSize

		val roi = Rect(x, y, width, height)

		return Mat(config.image, roi)
	}

	/**
	 * Get the position of the section
	 * @param section section to get the position
	 * @return position of the section
	 */
	fun sectionPosition(section: OMRSection): Rect {
		val (x, y) = config.getSectionPosition(section)
		val (width, height) = config.omrSectionSize

		return Rect(x, y, width, height)
	}
}
