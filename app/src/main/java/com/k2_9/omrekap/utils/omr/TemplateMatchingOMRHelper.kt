package com.k2_9.omrekap.utils.omr

import android.graphics.Bitmap
import com.k2_9.omrekap.data.configs.omr.OMRSection
import com.k2_9.omrekap.data.configs.omr.TemplateMatchingOMRHelperConfig
import com.k2_9.omrekap.utils.ImageAnnotationHelper
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.imgproc.Imgproc

/**
 * Helper for Optical Mark Recognition (OMR) using template matching
 * @param config configuration for the OMR helper
 */
class TemplateMatchingOMRHelper(private val config: TemplateMatchingOMRHelperConfig) :
	OMRHelper(config) {
	private var currentSectionGray: Mat? = null
	private var currentSectionBinary: Mat? = null

	/** Get the rectangles of the matched template in the current section
	 * @return list of pairs of rectangles and their similarity scores
	 */
	private fun getMatchRectangles(): List<Pair<Rect, Double>> {
		// Load the template image
		val template = config.template

		// Apply binary thresholding to the template image
		val templateBinary = Mat()
		Imgproc.threshold(
			template,
			templateBinary,
			0.0,
			255.0,
			Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_TRIANGLE,
		)

		// Perform template matching
		val result = Mat()
		Imgproc.matchTemplate(
			currentSectionBinary,
			templateBinary,
			result,
			Imgproc.TM_CCOEFF_NORMED,
		)

		// Set a threshold for template matching result
		val threshold = config.similarityThreshold

		val locations = mutableListOf<Point>()

		// Iterate through the result matrix
		for (y in 0 until result.rows()) {
			for (x in 0 until result.cols()) {
				val similarityScore = result.get(y, x)[0]

				if (similarityScore > threshold) {
					// Add the location to the list
					locations.add(Point(x.toDouble(), y.toDouble()))
				}
			}
		}

		// Get the bounding rectangles for the matched locations
		val matchedRectangles = ArrayList<Pair<Rect, Double>>()
		for (point in locations) {
			val locX = point.x.toInt()
			val locY = point.y.toInt()
			val rect = Rect(locX, locY, template!!.width(), template.height())
			matchedRectangles.add(Pair(rect, result.get(locY, locX)[0]))
		}

		return matchedRectangles
	}

	/** Get the contour information from the matched rectangles
	 * @param matchedRectangles list of pairs of rectangles and their similarity scores
	 * @return pair of list of contour information and list of similarity scores
	 */
	private fun getContourInfos(matchedRectangles: List<Pair<Rect, Double>>): Pair<List<ContourInfo>, List<Double>> {
		// Initialize a set to keep track of added rectangles
		val addedRectangles = mutableSetOf<Rect>()

		val contourInfos = mutableListOf<ContourInfo>()
		val similarities = matchedRectangles.map { it.second }

		// Iterate through the rectangles
		for (rect in matchedRectangles) {
			val x = rect.first.x
			val y = rect.first.y
			val w = rect.first.width
			val h = rect.first.height

			// Calculate the center of the rectangle
			val centerX = x + w / 2
			val centerY = y + h / 2

			// Check if the rectangle overlaps with any of the added rectangles
			var overlap = false
			for (addedRect in addedRectangles) {
				if (centerX >= addedRect.x && centerX <= addedRect.x + addedRect.width &&
					centerY >= addedRect.y && centerY <= addedRect.y + addedRect.height
				) {
					overlap = true
					break
				}
			}

			// If the rectangle does not overlap, add it to contour_info and the set of added rectangles
			if (!overlap) {
				contourInfos.add(ContourInfo(Pair(centerX, centerY), Pair(w, h)))
				addedRectangles.add(Rect(x, y, w, h))
			}
		}

		// Sort contourInfos by center_x
		val sortedContourInfos = contourInfos.sortedBy { it.center.first }

		// Zip sorted contourInfos with similarities
		val zippedContourInfos = sortedContourInfos.zip(similarities)

		// Unzip zipped contourInfos to separate lists
		val (sortedContours, sortedSimilarities) = zippedContourInfos.unzip()

		return Pair(sortedContours, sortedSimilarities)
	}

	/** Annotation for the image with the detected filled circles
	 * @param contourNumber detected number for the filled circles
	 * @return annotated image as Bitmap
	 */
	fun annotateImage(contourNumber: Int): Bitmap {
		val annotatedImg = currentSectionGray!!.clone()
		val matchedRectangles = getMatchRectangles()
		val res =
			ImageAnnotationHelper.annotateTemplateMatchingOMR(
				annotatedImg,
				matchedRectangles.map { it.first },
				contourNumber,
			)

		// Convert the annotated Mat to Bitmap
		val annotatedImageBitmap =
			Bitmap.createBitmap(
				res.width(),
				res.height(),
				Bitmap.Config.ARGB_8888,
			)
		Utils.matToBitmap(res, annotatedImageBitmap)
		return annotatedImageBitmap
	}

	/** Detect the filled circles in the section
	 * @param section the OMR section to detect
	 * @return detected number for the filled circles
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

		val matchedRectangles = getMatchRectangles()

		val contourInfos = getContourInfos(matchedRectangles)
		val filteredContourInfos = filterContourInfos(contourInfos.first, contourInfos.second)

		if (filteredContourInfos.size != 3) {
			throw DetectionError("Failed to detect 3 filled circle")
		}

		return contourInfosToNumbers(filteredContourInfos.toList())
	}
}
