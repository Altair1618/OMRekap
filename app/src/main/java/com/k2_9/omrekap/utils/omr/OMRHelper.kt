package com.k2_9.omrekap.utils.omr

import com.k2_9.omrekap.data.configs.omr.OMRHelperConfig
import com.k2_9.omrekap.data.configs.omr.OMRSection
import kotlin.math.abs
import kotlin.math.floor

abstract class OMRHelper(private val config: OMRHelperConfig) {
	class ContourInfo(val center: Pair<Int, Int>, val size: Pair<Int, Int>) {
		fun isOverlapping(other: ContourInfo): Boolean {
			return isColumnOverlapping(other) && isRowOverlapping(other)
		}

		fun isColumnOverlapping(other: ContourInfo): Boolean {
			val x1 = center.first
			val x2 = other.center.first
			val w1 = size.first
			val w2 = other.size.first

			return abs(x1 - x2) * 2 < w1 + w2
		}

		fun isRowOverlapping(other: ContourInfo): Boolean {
			val y1 = center.second
			val y2 = other.center.second
			val h1 = size.second
			val h2 = other.size.second

			return abs(y1 - y2) * 2 < h1 + h2
		}
	}

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

	protected fun filterContourInfos(
		contourInfos: List<ContourInfo>,
		filledIntensities: List<Double>,
	): List<ContourInfo?> {
		val mutableContourInfos = contourInfos.toMutableList()
		val uniqueContourInfos = mutableListOf<ContourInfo?>()

		// Group by overlapping contour infos and choose the one with the highest intensity
		for (i in 0 until mutableContourInfos.size - 1) {
			if (mutableContourInfos[i].isColumnOverlapping(mutableContourInfos[i + 1])) {
				if (filledIntensities[i] > filledIntensities[i + 1]) {
					mutableContourInfos[i + 1] = mutableContourInfos[i]
				}
				continue
			} else {
				uniqueContourInfos.add(mutableContourInfos[i])
			}
		}

		if (mutableContourInfos.isNotEmpty()) {
			uniqueContourInfos.add(mutableContourInfos.last())
		}

		assert(uniqueContourInfos.size <= 3)

		val sectionWidth = config.omrCropper.config.omrSectionSize.first
		val finalContourInfos = arrayOfNulls<ContourInfo>(3)

		uniqueContourInfos.forEach { contourInfo ->
			if (contourInfo != null) {
				val centerX = contourInfo.center.first
				val columnIndex = floor((centerX.toDouble() / sectionWidth) * 3).toInt()
				finalContourInfos[columnIndex] = contourInfo
			}
		}

		return finalContourInfos.toList()
	}

	abstract fun detect(section: OMRSection): Int
}
