package com.k2_9.omrekap.utils.omr

import android.graphics.Bitmap
import com.k2_9.omrekap.data.configs.omr.OMRSection
import com.k2_9.omrekap.data.configs.omr.TemplateMatchingOMRDetectorConfig
import com.k2_9.omrekap.utils.ImageAnnotationHelper
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.imgproc.Imgproc
import kotlin.collections.ArrayList

class TemplateMatchingOMRHelper(private val config: TemplateMatchingOMRHelperConfig) : OMRHelper(config) {
	private var currentSectionGray: Mat? = null
	private var currentSectionBinary: Mat? = null

	private fun getMatchRectangles(): List<Rect> {
		// TODO: fix algorithm bug

		// Load the template image
		val template = config.template

		// Apply binary thresholding to the template image
		val templateBinary = Mat()
		Imgproc.threshold(template, templateBinary, 0.0, 255.0, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_TRIANGLE)

		// Perform template matching
		val result = Mat()
		Imgproc.matchTemplate(currentSectionBinary, templateBinary, result, Imgproc.TM_CCOEFF_NORMED)

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
		val matchedRectangles = ArrayList<Rect>()
		for (point in locations) {
			val locX = point.x.toInt()
			val locY = point.y.toInt()
			val rect = Rect(locX, locY, template.width(), template.height())
			matchedRectangles.add(rect)
		}

		return matchedRectangles
	}

	private fun getContourInfos(matchedRectangles: List<Rect>): List<ContourInfo?> {
		// Initialize a set to keep track of added rectangles
		val addedRectangles = mutableSetOf<Rect>()

		val contourInfos = mutableListOf<ContourInfo>()

		// Iterate through the rectangles
		for (rect in matchedRectangles) {
			val x = rect.x
			val y = rect.y
			val w = rect.width
			val h = rect.height

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

		// short by center_x
		contourInfos.sortBy { it.center.first }

		return contourInfos.toList()
	}
	fun annotateImage(contourNumber: Int) :Bitmap{
		val annotatedImg = currentSectionGray!!.clone()
		val matchedRectangles = getMatchRectangles()
		for (rect in matchedRectangles) {
			ImageAnnotationHelper.annotateOMR(annotatedImg, rect, contourNumber)
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

		val matchedRectangles = getMatchRectangles()

		val contourInfos = getContourInfos(matchedRectangles)
		val filteredContourInfos = filterContourInfos(contourInfos.toList())

		return contourInfosToNumbers(filteredContourInfos.toList())
	}
}
