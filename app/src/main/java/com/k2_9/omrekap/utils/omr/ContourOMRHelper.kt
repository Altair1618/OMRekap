package com.k2_9.omrekap.utils.omr

import android.graphics.Bitmap
import android.util.Log
import com.k2_9.omrekap.data.configs.omr.ContourOMRHelperConfig
import com.k2_9.omrekap.data.configs.omr.OMRSection
import com.k2_9.omrekap.utils.ImageAnnotationHelper
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sin

/**
 * Helper for Optical Mark Recognition (OMR) using contours
 * @param config configuration for the OMR helper
 */
class ContourOMRHelper(private val config: ContourOMRHelperConfig) : OMRHelper(config) {
	private var currentSectionGray: Mat? = null
	private var currentSectionBinary: Mat? = null

	/**
	 * Create information object about the contour
	 * @param center center of the contour
	 * @param size size of the contour
	 * @return ContourInfo object
	 */
	private fun createContourInfo(contour: Mat): ContourInfo {
		val rect = Imgproc.boundingRect(contour)
		val centerX = rect.x + rect.width / 2
		val centerY = rect.y + rect.height / 2
		return ContourInfo(Pair(centerX, centerY), Pair(rect.width, rect.height))
	}

	/**
	 * Filter contours based on the intensities and return the filtered contour infos
	 * @param contourInfos list of contour infos
	 * @param intensities list of intensities
	 * @return filtered list of contour infos
	 */
	private fun getContourInfo(
		filledContours: List<Mat>,
		filledIntensities: List<Int>,
	): List<ContourInfo?> {
		val contourInfos = mutableListOf<ContourInfo>()

		// Zip filledContours with filledIntensities
		val contoursWithIntensities = filledContours.zip(filledIntensities)

		// Sort contours and intensities based on the x-coordinate of bounding rectangles
		val sortedContoursWithIntensities = contoursWithIntensities.sortedBy { (contour, _) -> Imgproc.boundingRect(contour).x }

		// Unzip sorted contours and intensities
		val (sortedContours, sortedIntensities) = sortedContoursWithIntensities.unzip()

		// Get contour info for each sorted contour
		for (contour in sortedContours) {
			contourInfos.add(createContourInfo(contour))
		}

		// Filter contour infos with sorted intensities
		return filterContourInfos(contourInfos, sortedIntensities.map { it.toDouble() })
	}

	/**
	 * Predict the number based on the detected filled circle contours
	 * @param contours list of filled circle contours
	 * @return predicted number
	 */
	private fun predictForFilledCircle(contours: List<MatOfPoint>): Int {
		// Predict the number based on the filled circle contours

		val filledContours = mutableListOf<Mat>()
		val filledIntensities = mutableListOf<Int>()

		for (contour in contours) {
			val mask = Mat.zeros(currentSectionBinary!!.size(), CvType.CV_8UC1)
			Imgproc.drawContours(mask, listOf(contour), -1, Scalar(255.0), -1)

			// Apply the mask to the binary image
			val maskedBinary = Mat()
			Core.bitwise_and(currentSectionBinary!!, currentSectionBinary!!, maskedBinary, mask)

			// Compute the total intensity (number of non-zero pixels) within the contour area
			val totalIntensity = Core.countNonZero(maskedBinary)

			// Compute the total number of pixels within the contour area
			val contourArea = Imgproc.contourArea(contour)

			// Compute the percentage of dark pixels inside the contour
			val percentageDarkPixels = totalIntensity.toDouble() / contourArea

			if (totalIntensity > config.darkIntensityThreshold &&
				percentageDarkPixels >= config.darkPercentageThreshold
			) {
				filledContours.add(contour)
				filledIntensities.add(totalIntensity)
			}
		}

		val contourInfos = getContourInfo(filledContours, filledIntensities)

		if (contourInfos.size != 3) {
			throw DetectionError("Failed to detect 3 filled circle")
		}

		return contourInfosToNumbers(contourInfos)
	}

	/**
	 * Get the darkest row in the column
	 * @param colContours list of contours in the column
	 * @return index of the darkest row
	 */
	private fun getDarkestRow(colContours: List<MatOfPoint>): Int? {
		// Initialize variables to store the darkest row and its intensity
		var darkestRow: Int? = null
		var darkestIntensity = 0.0

		// Loop through contours in the column
		for ((idx, contour) in colContours.withIndex()) {
			// Construct a mask for the current contour
			val mask = Mat.zeros(currentSectionBinary!!.size(), CvType.CV_8UC1)
			Imgproc.drawContours(mask, listOf(contour), -1, Scalar(255.0), -1)

			// # Apply the mask to the binary image
			val maskedBinary = Mat()
			Core.bitwise_and(currentSectionBinary!!, currentSectionBinary!!, maskedBinary, mask)

			// Compute the total intensity (number of non-zero pixels) within the contour area
			val totalIntensity = Core.countNonZero(maskedBinary)

			// Compute the total number of pixels within the contour area
			val contourArea = Imgproc.contourArea(contour)

			// Compute the percentage of dark pixels inside the contour
			val percentageDarkPixels = totalIntensity.toDouble() / contourArea

			// Update the darkest row if necessary
			if (darkestIntensity < totalIntensity &&
				totalIntensity >= config.darkIntensityThreshold &&
				percentageDarkPixels >= config.darkPercentageThreshold
			) {
				darkestIntensity = totalIntensity.toDouble()
				darkestRow = idx
			}
		}
		if (darkestRow == null) {
			// If no darkest row is found, return null
			Log.e("ContourOMRHelper", "No darkest row found")
		}
		return darkestRow
	}

	/**
	 * Create a perfect circle contour
	 * @param x x-coordinate of the center
	 * @param y y-coordinate of the center
	 * @param radius radius of the circle
	 * @return perfect circle contour
	 */
	private fun getPerfectCircle(
		x: Double,
		y: Double,
		radius: Double,
	): MatOfPoint {
		val numPoints = 100 // Adjust as needed
		val theta = DoubleArray(numPoints) { it * 2 * Math.PI / numPoints }
		val circleX = DoubleArray(numPoints) { x + radius * cos(theta[it]) }
		val circleY = DoubleArray(numPoints) { y + radius * sin(theta[it]) }

		val circleContour = MatOfPoint()
		for (i in 0 until numPoints) {
			circleContour.push_back(MatOfPoint(Point(circleX[i], circleY[i])))
		}

		return circleContour
	}

	/**
	 * Replace the contour with a perfect circle
	 * @param contour contour to be replaced
	 * @return perfect circle contour
	 */
	private fun replaceWithPerfectCircle(contour: MatOfPoint): MatOfPoint {
		val rect = Imgproc.boundingRect(contour)
		val centroidX = rect.x + rect.width.toDouble() / 2
		val centroidY = rect.y + rect.height.toDouble() / 2
		val radius = maxOf(rect.width.toDouble(), rect.height.toDouble()) / 2

		return getPerfectCircle(centroidX, centroidY, radius)
	}

	/**
	 * Get the combined number from the darkest rows of each column, given 10 contours for each column
	 * @param darkestRows list of 10 detected contours for each column
	 * @return combined number
	 */
	private fun compareAll(contours: List<MatOfPoint>): Int {
		// Sort contours by column and then by row
		val contoursSorted = contours.sortedBy { Imgproc.boundingRect(it).x }

		// Initialize a list to store the darkest contour row for each column
		val darkestRows = mutableListOf<Int?>()

		// Loop through each column
		for (col in 0 until 3) {
			// Get contours for the current column and sort by rows
			val colContours =
				contoursSorted.subList(col * 10, (col + 1) * 10)
					.sortedBy { Imgproc.boundingRect(it).y }

			var darkestRow = getDarkestRow(colContours)

			darkestRow = darkestRow ?: 0

			// Append the darkest row for the current column to the list
			darkestRows.add(darkestRow)
		}

		darkestRows.forEachIndexed { idx, darkestRow ->
			if (darkestRow == null) {
				Log.e("ContourOMRHelper", "No darkest row found for column ${idx + 1}. Assuming 0")
			}
		}
		return getCombinedNumbers(darkestRows.map { it ?: 0 })
	}

	/**
	 * Complete missing contours by filling the missing circles
	 * @param contours list of detected contours
	 * @return list of completed contours
	 */
	private fun completeMissingContours(contours: List<MatOfPoint>): List<MatOfPoint> {
		val sortedContours = contours.sortedBy { Imgproc.boundingRect(it).y }
		val columnMap = Array(3) { mutableListOf<MatOfPoint>() }
		val rectColumnMap = Array(3) { mutableListOf<Rect>() }
		val sortedRects = sortedContours.map { Imgproc.boundingRect(it) }

		fun getColumnIndex(index: Int): Int {
			return floor((max(0.0, sortedRects[index].x.toDouble()) / config.omrCropper.config.omrSectionSize.first.toDouble()) * 3.0).toInt()
		}

		for ((idx, rect) in sortedRects.withIndex()) {
			val columnIndex = getColumnIndex(idx)
			columnMap[columnIndex].add(contours[idx])
			rectColumnMap[columnIndex].add(rect)
		}

		val averageX = DoubleArray(3)

		for ((idx, columns) in columnMap.withIndex()) {
			if (columns.isEmpty()) {
				// no contour in this column, skip entirely
				return contours
			}
			averageX[idx] = rectColumnMap[idx].sumOf { it.x + it.width / 2.0 } / columns.size
		}

		val result = mutableListOf<MatOfPoint>()
		val fillRecord = booleanArrayOf(false, false, false)
		var ySum = 0.0
		var radiusSum = 0.0
		var lowestY = -1

		var idx = 0

		fun getLowestY(index: Int) = sortedRects[index].y + sortedRects[index].height

		while (idx < sortedContours.size) {
			val contour = sortedContours[idx]
			val columnIndex = getColumnIndex(idx)
			val currentLowestY = getLowestY(idx)

			if (fillRecord[columnIndex] || (lowestY != -1 && (sortedRects[idx].y + sortedRects[idx].height / 2) > lowestY)) {
				val nonFilledColumn = (0 until 3).filter { !fillRecord[it] }
				val filledCount = 3 - nonFilledColumn.size

				if (filledCount == 0) {
					lowestY = currentLowestY
					continue
				}

				val y = ySum / filledCount
				val radius = radiusSum / filledCount

				for (i in nonFilledColumn) {
					val x = averageX[i]
					result.add(getPerfectCircle(x, y, radius))
					fillRecord[i] = true
				}
				lowestY = currentLowestY
			} else {
				result.add(contour)
				ySum += sortedRects[idx].y + sortedRects[idx].height.toDouble() / 2
				radiusSum += max(sortedRects[idx].width.toDouble(), sortedRects[idx].height.toDouble()) / 2
				fillRecord[columnIndex] = true
				idx++
				lowestY = max(lowestY, currentLowestY)
			}

			val allFilled = fillRecord.all { it }

			if (allFilled) {
				fillRecord.fill(false)
				ySum = 0.0
				radiusSum = 0.0
			}
		}

		val nonFilledColumn = (0 until 3).filter { !fillRecord[it] }
		val filledCount = 3 - nonFilledColumn.size

		if (filledCount > 0) {
			val y = ySum / filledCount
			val radius = radiusSum / filledCount

			for (i in nonFilledColumn) {
				val x = averageX[i]
				result.add(getPerfectCircle(x, y, radius))
				fillRecord[i] = true
			}
		}

		return result
	}

	/**
	 * Detect the circles in the OMR section
	 * @return list of detected contours
	 */
	private fun getAllContours(): List<MatOfPoint> {
		// Find circle contours in cropped OMR section
		val contours = mutableListOf<MatOfPoint>()
		val hierarchy = Mat()
		Imgproc.findContours(
			currentSectionBinary!!,
			contours,
			hierarchy,
			Imgproc.RETR_EXTERNAL,
			Imgproc.CHAIN_APPROX_SIMPLE,
		)

		// Initialize a list to store filtered contours
		val filteredContours = mutableListOf<MatOfPoint>()

		// Filter contours based on aspect ratio and size
		for (contour in contours) {
			val rect = Imgproc.boundingRect(contour)
			val ar = rect.width.toDouble() / rect.height.toDouble()
			val minLength = config.minRadius
			val maxLength = config.maxRadius
			val minAR = config.minAspectRatio
			val maxAR = config.maxAspectRatio

			if (rect.width in minLength..maxLength && rect.height in minLength..maxLength && ar >= minAR && ar <= maxAR) {
				filteredContours.add(replaceWithPerfectCircle(contour))
			} else {
				Log.d(
					"ContourOMRHelper",
					"Contour with aspect ratio $ar and size ${rect.width} x ${rect.height} filtered out",
				)
			}
		}

		if (filteredContours.size < 30) {
			Log.d(
				"ContourOMRHelper",
				"Detected ${filteredContours.size} contours, attempting to complete missing contours",
			)
			val completedContours = completeMissingContours(filteredContours)
			Log.d(
				"ContourOMRHelper",
				"Completed missing contours, now have ${completedContours.size} contours",
			)
		}

		return filteredContours
	}

	/**
	 * Detect the number for the OMR section
	 * @param contours list of detected contours
	 * @return detected number
	 */
	override fun detect(section: OMRSection): Int {
		val omrSectionImage = config.omrCropper.crop(section)

		// Convert image to grayscale if it is not
		val gray =
			if (omrSectionImage.channels() != 1) {
				val grayImageMat = Mat()
				Imgproc.cvtColor(omrSectionImage, grayImageMat, Imgproc.COLOR_BGR2GRAY)
				grayImageMat
			} else {
				omrSectionImage
			}

		// Apply binary thresholding
		val binary = Mat()
		Imgproc.threshold(
			gray,
			binary,
			0.0,
			255.0,
			Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_TRIANGLE,
		)

		// Update states
		currentSectionGray = gray
		currentSectionBinary = binary

		val contours = getAllContours()

		return if (contours.size != 30) {
			Log.d(
				"ContourOMRHelper",
				"Some circles are not detected, considering only filled circles",
			)
			predictForFilledCircle(contours)
		} else {
			Log.d("ContourOMRHelper", "All 30 circles are detected")
			compareAll(contours)
		}
	}

	/**
	 * Get the position of the OMR section
	 * @param section OMR section
	 * @return position of the OMR section
	 */
	fun getSectionPosition(section: OMRSection): Rect {
		return config.omrCropper.sectionPosition(section)
	}

	/**
	 * Annotate the image with the detected contour
	 * @param contourNumber detected contour number
	 * @return annotated image
	 */
	fun annotateImage(contourNumber: Int): Bitmap {
		var annotatedImg = currentSectionGray!!.clone()
		val contours = getAllContours()
		annotatedImg =
			ImageAnnotationHelper.annotateContourOMR(annotatedImg, contours, contourNumber)

		val annotatedImageBitmap =
			Bitmap.createBitmap(
				annotatedImg.width(),
				annotatedImg.height(),
				Bitmap.Config.ARGB_8888,
			)
		Utils.matToBitmap(annotatedImg, annotatedImageBitmap)
		return annotatedImageBitmap
	}
}
