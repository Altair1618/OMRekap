package com.k2_9.omrekap.utils.omr

import android.graphics.Bitmap
import android.util.Log
import com.k2_9.omrekap.data.configs.omr.ContourOMRDetectorConfig
import com.k2_9.omrekap.data.configs.omr.OMRSection
import com.k2_9.omrekap.utils.ImageAnnotationHelper
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class ContourOMRHelper(private val config: ContourOMRDetectorConfig) : OMRHelper(config) {
	private var currentSectionGray: Mat? = null
	private var currentSectionBinary: Mat? = null

	private fun createContourInfo(contour: Mat): ContourInfo {
		val rect = Imgproc.boundingRect(contour)
		val centerX = rect.x + rect.width / 2
		val centerY = rect.y + rect.height / 2
		return ContourInfo(Pair(centerX, centerY), Pair(rect.width, rect.height))
	}

	private fun getContourInfo(filledContours: List<Mat>): List<ContourInfo?> {
		val contourInfos = mutableListOf<ContourInfo?>()
		val sortedContours = filledContours.sortedBy { Imgproc.boundingRect(it).x }

		// Sort the filled contours from left to right and get the center and size of each contour
		for (contour in sortedContours) {
			contourInfos.add(createContourInfo(contour))
		}
		return filterContourInfos(contourInfos)
	}

	private fun predictForFilledCircle(contours: List<MatOfPoint>): Int {
		// Predict the number based on the filled circle contours

		val filledContours = mutableListOf<Mat>()

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
			}
		}

		val contourInfos = getContourInfo(filledContours)
		return contourInfosToNumbers(contourInfos)
	}

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

	private fun compareAll(contours: List<MatOfPoint>): Int {
		// Sort contours by column and then by row
		val contoursSorted = contours.sortedBy { Imgproc.boundingRect(it).x }

		// Initialize a list to store the darkest contour row for each column
		val darkestRows = mutableListOf<Int?>()

		// Loop through each column
		for (col in 0 until 3) {
			// Get contours for the current column and sort by rows
			val colContours = contoursSorted.subList(col * 10, (col + 1) * 10).sortedBy { Imgproc.boundingRect(it).y }

			val darkestRow = getDarkestRow(colContours)

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

	private fun getAllContours(): List<MatOfPoint> {
		// Find circle contours in cropped OMR section
		val contours = mutableListOf<MatOfPoint>()
		val hierarchy = Mat()
		Imgproc.findContours(currentSectionBinary!!, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

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
				filteredContours.add(contour)
			} else {
				Log.d("ContourOMRHelper", "Contour with aspect ratio $ar and size ${rect.width} x ${rect.height} filtered out")
			}
		}

		return filteredContours
	}

	private fun annotateImage(contourNumber: Int): Bitmap {
		var annotatedImg = currentSectionGray!!.clone()
		val contours = getAllContours()
		for (contour in contours) {
			val rect = Imgproc.boundingRect(contour)
			annotatedImg = ImageAnnotationHelper.annotateOMR(annotatedImg, rect, contourNumber)
		}
		val annotatedImageBitmap = Bitmap.createBitmap(annotatedImg.width(), annotatedImg.height(), Bitmap.Config.ARGB_8888)
		Utils.matToBitmap(annotatedImg, annotatedImageBitmap)
		return annotatedImageBitmap
	}


	override fun detect(section: OMRSection): Int {
		val omrSectionImage = config.omrCropper.crop(section)

		// Convert image to grayscale
		val gray = Mat()
		Imgproc.cvtColor(omrSectionImage, gray, Imgproc.COLOR_BGR2GRAY)

		// Apply binary thresholding
		val binary = Mat()
		Imgproc.threshold(gray, binary, 0.0, 255.0, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_TRIANGLE)

		// Update states
		currentSectionGray = gray
		currentSectionBinary = binary

		val contours = getAllContours()

		return if (contours.size != 30) {
			Log.d("ContourOMRHelper", "Some circles are not detected, considering only filled circles")
			predictForFilledCircle(contours)

		} else {
			Log.d("ContourOMRHelper", "All 30 circles are detected")
			compareAll(contours)
		}
	}

}
