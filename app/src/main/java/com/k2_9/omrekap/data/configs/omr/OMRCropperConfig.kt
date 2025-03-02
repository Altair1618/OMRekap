package com.k2_9.omrekap.data.configs.omr

import org.opencv.core.Mat

/**
 * Configuration for the cropper
 * @param image image to be cropped
 * @param omrSectionSize size of the OMR section
 * @param omrSectionPosition position of the OMR section
 */
class OMRCropperConfig(
	image: Mat?,
	val omrSectionSize: Pair<Int, Int>,
	omrSectionPosition: Map<OMRSection, Pair<Int, Int>>,
) {
	var image: Mat?
		private set
		get() = field?.clone()

	private val omrSectionPosition: Map<OMRSection, Pair<Int, Int>>

	// Check if all the sections are present
	init {

		// Note: Top-left corner and height must be in the way so that the section is cropped with additional top padding and no bottom padding
		// Top padding must have the same size as gap between circles inside the section

		require(omrSectionSize.first >= 0 && omrSectionSize.second >= 0) {
			"OMR section size must be non-negative"
		}

		require(omrSectionPosition.keys.containsAll(OMRSection.entries)) {
			"All OMR sections must be present"
		}

		require(omrSectionPosition.values.all { it.first >= 0 && it.second >= 0 }) {
			"OMR section position must be non-negative"
		}

		this.image = null
		this.omrSectionPosition = omrSectionPosition.toMap()

		if (image != null) {
			setImage(image)
		}
	}

	/**
	 * Get the position of the section
	 * @param section section to get the position
	 * @return position of the section
	 */
	fun getSectionPosition(section: OMRSection): Pair<Int, Int> {
		return omrSectionPosition[section]!!
	}

	/**
	 * Set the image
	 * @param image image to be set
	 */
	fun setImage(image: Mat) {
		require(omrSectionSize.first <= image.width() && omrSectionSize.second <= image.height()) {
			"OMR section size must be less than or equal to the image size"
		}

		require(omrSectionPosition.values.all { it.first <= image.width() && it.second <= image.height() }) {
			"OMR section position must be less than the image size"
		}

		this.image = image.clone()
	}
}
