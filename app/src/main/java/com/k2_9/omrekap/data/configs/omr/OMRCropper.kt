package com.k2_9.omrekap.data.configs.omr

import org.opencv.core.Mat
import org.opencv.core.Rect

class OMRCropper(val config: OMRCropperConfig) {
	fun crop(section: OMRSection): Mat {
		val (x, y) = config.getSectionPosition(section)
		val (width, height) = config.omrSectionSize

		val roi = Rect(x, y, width, height)

		return Mat(config.image, roi)
	}
}
