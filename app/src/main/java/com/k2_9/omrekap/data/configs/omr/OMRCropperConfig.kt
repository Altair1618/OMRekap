package com.k2_9.omrekap.data.configs.omr

import org.opencv.core.Mat


class OMRCropperConfig(
	image: Mat,
	val omrSectionSize: Pair<Int, Int>, // (width, height)
	omrSectionPosition: Map<OMRSection, Pair<Int, Int>> // {OMRSection: (x, y)}
) {
	var image: Mat
		private set
		get() = field.clone()

	// Check if all the sections are present
	init {
		require (omrSectionPosition.keys.containsAll(OMRSection.entries)) {
			"All OMR sections must be present"
		}

		require(omrSectionSize.first >= 0 && omrSectionSize.second >= 0) {
			"OMR section size must be non-negative"
		}

		require(omrSectionPosition.values.all { it.first >= 0 && it.second >= 0 }) {
			"OMR section position must be non-negative"
		}

		require(omrSectionSize.first <= image.width() && omrSectionSize.second <= image.height()) {
			"OMR section size must be less than or equal to the image size"
		}

		this.image = image.clone()
	}

	private val omrSectionPosition: Map<OMRSection, Pair<Int, Int>> = omrSectionPosition.toMap()

	fun getImage(): Mat {
		return image.clone()
	}

	fun getOmrSectionSize(): Pair<Int, Int> {
		return omrSectionSize
	}

	fun getSectionPosition(section: OMRSection): Pair<Int, Int> {
		return omrSectionPosition[section]!!
	}
}
