package com.k2_9.omrekap.utils.omr

import com.k2_9.omrekap.data.configs.omr.OMRHelperConfig
import com.k2_9.omrekap.data.configs.omr.OMRSection
import kotlin.math.floor

abstract class OMRHelper(private val config: OMRHelperConfig) {
	data class ContourInfo(val center: Pair<Int, Int>, val size: Pair<Int, Int>)

	class DetectionError(message: String) : Exception(message)

	protected fun getCombinedNumbers(numbers: List<Int>): Int {
		// Combine the detected numbers into a single integer
		return numbers.joinToString("").toInt()
	}

	protected fun contourInfosToNumbers(contourInfos: List<ContourInfo?>): Int {
		// Return the detected numbers based on the vertical position of the filled circles for each column
		if (contourInfos.size != 3) {
			throw DetectionError("Filled circles are not detected correctly")
		}

		val columnHeight = config.omrCropper.config.omrSectionSize.second // Define the column height based on your image

		val result = mutableListOf<Int>()

		for (contourInfo in contourInfos) {
			if (contourInfo == null) {
				// user might accidentally leave the column with no filled circle for 0 value
				result.add(0)
			} else {
				// Detect number based on vertical position of the contour
				val centerY = contourInfo.center.second
				val columnIndex = floor((centerY.toDouble() / columnHeight.toDouble()) * 10).toInt()

				result.add(columnIndex)
			}
		}
		return getCombinedNumbers(result)
	}

	protected fun filterContourInfos(contourInfos: List<ContourInfo?>): List<ContourInfo?> {
		// TODO: Handle when 1 column has more than 1 filled circle
		// TODO: Handle when no filled circle for each column (assume that the number is 0, with null as representation of the ContourInfo)

		return contourInfos
	}

	abstract fun detect(section: OMRSection): Int

	fun detect(): Map<OMRSection, Int> {
		val results = mutableMapOf<OMRSection, Int>()

		for (section in OMRSection.entries) {
			results[section] = detect(section)
		}

		return results.toMap()
	}
}
