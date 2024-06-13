package com.k2_9.omrekap.utils.omr

import com.k2_9.omrekap.data.configs.omr.OMRHelperConfig
import com.k2_9.omrekap.data.configs.omr.OMRSection
import kotlin.math.abs
import kotlin.math.floor

/**
 * Helper for Optical Mark Recognition (OMR)
 * @param config configuration for the OMR helper
 */
abstract class OMRHelper(private val config: OMRHelperConfig) {
	/**
	 * Information about the contour
	 * @param center center of the contour
	 * @param size size of the contour
	 */
	class ContourInfo(val center: Pair<Int, Int>, val size: Pair<Int, Int>) {
		/** Check if the contour is overlapping with another contour
		 * @param other other contour to check
		 * @return true if the contour is overlapping with the other contour, false otherwise
		 */
		fun isOverlapping(other: ContourInfo): Boolean {
			return isColumnOverlapping(other) && isRowOverlapping(other)
		}

		/** Check if the contour is overlapping with another contour horizontally
		 * @param other other contour to check
		 * @return true if the contour is overlapping with the other contour horizontally, false otherwise
		 */
		fun isColumnOverlapping(other: ContourInfo): Boolean {
			val x1 = center.first
			val x2 = other.center.first
			val w1 = size.first
			val w2 = other.size.first

			return abs(x1 - x2) * 2 < w1 + w2
		}

		/** Check if the contour is overlapping with another contour vertically
		 * @param other other contour to check
		 * @return true if the contour is overlapping with the other contour vertically, false otherwise
		 */
		fun isRowOverlapping(other: ContourInfo): Boolean {
			val y1 = center.second
			val y2 = other.center.second
			val h1 = size.second
			val h2 = other.size.second

			return abs(y1 - y2) * 2 < h1 + h2
		}
	}

	/**
	 * Error when detecting the filled circles
	 * @param message error message
	 */
	class DetectionError(message: String) : Exception(message)

	/**
	 * Combine the detected numbers into a single integer
	 * @param numbers list of detected numbers
	 * @return combined numbers
	 */
	protected fun getCombinedNumbers(numbers: List<Int>): Int {
		// Combine the detected numbers into a single integer
		return numbers.joinToString("").toInt()
	}

	/**
	 * Convert contour infos to numbers
	 * @param contourInfos list of contour infos
	 * @return detected numbers
	 */
	protected fun contourInfosToNumbers(contourInfos: List<ContourInfo?>): Int {
		// Return the detected numbers based on the vertical position of the filled circles for each column
		if (contourInfos.size != 3) {
			throw DetectionError("Filled circles are not detected correctly")
		}

		val columnHeight =
			config.omrCropper.config.omrSectionSize.second // Define the column height based on your image

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

	/**
	 * Filter contour infos:
	 * remove overlapping contour infos and choose the one with the highest intensity
	 * automatically assign null to the column with no filled circle
	 * @param contourInfos list of contour infos
	 * @param filledIntensities list of filled intensities
	 * @return filtered contour infos
	 */
	protected fun filterContourInfos(
		contourInfos: List<ContourInfo>,
		filledIntensities: List<Double>,
	): List<ContourInfo?> {
		val mutableContourInfos = contourInfos.toMutableList()
		val uniqueContourInfos = mutableListOf<ContourInfo?>()
		val filledIntensitiesCopy = filledIntensities.toMutableList()

		// Group by overlapping contour infos and choose the one with the highest intensity
		for (i in 0 until mutableContourInfos.size - 1) {
			if (mutableContourInfos[i].isColumnOverlapping(mutableContourInfos[i + 1])) {
				if (filledIntensitiesCopy[i] > filledIntensitiesCopy[i + 1]) {
					mutableContourInfos[i + 1] = mutableContourInfos[i]
					filledIntensitiesCopy[i + 1] = filledIntensitiesCopy[i]
				}
				continue
			} else {
				uniqueContourInfos.add(mutableContourInfos[i])
			}
		}

		if (mutableContourInfos.isNotEmpty()) {
			uniqueContourInfos.add(mutableContourInfos.last())
		}

		assert(uniqueContourInfos.size <= config.columnCount)

		val sectionWidth = config.omrCropper.config.omrSectionSize.first
		val finalContourInfos = arrayOfNulls<ContourInfo>(config.columnCount)

		uniqueContourInfos.forEach { contourInfo ->
			if (contourInfo != null) {
				val centerX = contourInfo.center.first
				val columnIndex =
					floor((centerX.toDouble() / sectionWidth) * config.columnCount).toInt()
				finalContourInfos[columnIndex] = contourInfo
			}
		}

		return finalContourInfos.toList()
	}

	abstract fun detect(section: OMRSection): Int
}
