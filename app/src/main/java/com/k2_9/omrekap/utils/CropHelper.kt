package com.k2_9.omrekap.utils

import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.k2_9.omrekap.data.models.CornerPoints
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY
import org.opencv.imgproc.Imgproc.cvtColor
import org.opencv.imgproc.Imgproc.getPerspectiveTransform
import org.opencv.imgproc.Imgproc.warpPerspective
import kotlin.math.pow
import kotlin.math.sqrt

object CropHelper {
	private const val UPPER_LEFT: Int = 0
	private const val UPPER_RIGHT: Int = 1
	private const val LOWER_RIGHT: Int = 2
	private const val LOWER_LEFT: Int = 3

	private lateinit var pattern: Mat

	/**
	 * Uses OpenCV module, remember OpenCVLoader.initLocal() has been run before
	 * load corner pattern
	 *
	 * @param patternBitmap corner pattern in Bitmap
	 */
	fun loadPattern(patternBitmap: Bitmap) {
		// Load only if pattern hasn't been loaded
		if (::pattern.isInitialized) return

		this.pattern = Mat(patternBitmap.height, patternBitmap.width, CvType.CV_8UC1)
		val cv8uc4pattern = Mat(patternBitmap.height, patternBitmap.width, CvType.CV_8UC1)
		Utils.bitmapToMat(patternBitmap, cv8uc4pattern)
		cvtColor(cv8uc4pattern, this.pattern, COLOR_BGR2GRAY)

		this.pattern = PreprocessHelper.preprocessPattern(this.pattern)
	}

	/**
	 * Uses OpenCV module, remember OpenCVLoader.initLocal() has been run before
	 *
	 * todo @exception if corner found are bad
	 *
	 * Initialize corner pattern first using [CropHelper.loadPattern]
	 *
	 *
	 * @param img Mat that has been scaled, in grayscale (CV_8UC1)
	 *
	 * @return CornerPoints if four corners are found
	 */
	fun detectCorner(img: Mat): CornerPoints {
		// If pattern hasn't been loaded, throw exception
		if (!::pattern.isInitialized) {
			throw Exception("Pattern not loaded!")
		}

		var imgGray = img.clone()
		cvtColor(img, imgGray, COLOR_BGR2GRAY)

		// do local normalization here
		imgGray = localNormalize(imgGray)

		val resultMatrix =
			Mat(
				img.height() - pattern.height() + 1,
				img.width() - pattern.width() + 1,
				CvType.CV_8UC1,
			)

		Imgproc.matchTemplate(imgGray, pattern, resultMatrix, Imgproc.TM_SQDIFF_NORMED)

		var upperLeftPoint = Point()
		var upperRightPoint = Point()
		var lowerLeftPoint = Point()
		var lowerRightPoint = Point()

		val needed = mutableListOf(true, true, true, true)
		var needChange = 4

		data class PointsAndWeight(
			val x: Int,
			val y: Int,
			val weight: Double,
		)

		val pointsList: MutableList<PointsAndWeight> = mutableListOf()

		for (i in 0 until resultMatrix.height() step 2) {
			for (j in 0 until resultMatrix.width() step 2) {
				pointsList.add(PointsAndWeight(i, j, resultMatrix.get(i, j)[0]))
			}
		}

		pointsList.sortBy { it.weight }

		pointsList.forEach {
			if (needChange == 0) return@forEach

			val corner = nearWhichCorner(it.x, it.y, resultMatrix.height(), resultMatrix.width(), limFrac = 0.4F)
			if (corner == -1) return@forEach

			if (it.weight > 0.45) {
				// Corner not found, throw exception
				val exceptionMessage = "Not all corners found: {" +
					(if (needed[0]) "Upper left," else "") +
					(if (needed[1]) "Upper right," else "") +
					(if (needed[2]) "Lower right," else "") +
					(if (needed[3]) "Lower left," else "") +
					"}"
				// throw NotFoundException(exceptionMessage);
				Log.e("Corner",  exceptionMessage);
			}

			if (needed[corner]) {
				needed[corner] = false
				needChange--
				val pointFromIt = Point(it.y.toDouble(), it.x.toDouble())
				Log.d("Corner", "Difference: ${it.weight}; corner: $corner")
				when (corner) {
					UPPER_LEFT -> {
						upperLeftPoint = pointFromIt
					}
					UPPER_RIGHT -> {
						upperRightPoint = pointFromIt
						upperRightPoint.x += pattern.height().toDouble()
					}
					LOWER_RIGHT -> {
						lowerRightPoint = pointFromIt
						lowerRightPoint.x += pattern.height().toDouble()
						lowerRightPoint.y += pattern.width().toDouble()
					}
					LOWER_LEFT -> {
						lowerLeftPoint = pointFromIt
						lowerLeftPoint.y += pattern.width().toDouble()
					}
				}
			}
		}

		if (needChange > 0) {
			throw Exception("Not all corner points found!")
		}

		Log.d("Corner", CornerPoints(upperLeftPoint, upperRightPoint, lowerRightPoint, lowerLeftPoint).toString())
		return CornerPoints(upperLeftPoint, upperRightPoint, lowerRightPoint, lowerLeftPoint)
	}

	/**
	 * Uses OpenCV module, remember OpenCVLoader.initLocal() has been run before
	 *
	 * Crop an image based on four corners, with some padding set by #mult
	 *
	 * @param img image to crop
	 * @param points corner points to crop image
	 *
	 * @return cropped image
	 */
	fun fourPointTransform(
		img: Mat,
		points: CornerPoints,
	): Mat {
		// Multiplier for padding, can be adjusted
		val mult = 0.01

		// Calculate new corner points
		val newTopLeft =
			Point(
				points.topLeft.x - (points.topRight.x - points.topLeft.x) * mult - (points.bottomLeft.x - points.topLeft.x) * mult,
				points.topLeft.y - (points.topRight.y - points.topLeft.y) * mult - (points.bottomLeft.y - points.topLeft.y) * mult,
			)

		val newTopRight =
			Point(
				points.topRight.x + (points.topRight.x - points.topLeft.x) * mult - (points.bottomRight.x - points.topRight.x) * mult,
				points.topRight.y + (points.topRight.y - points.topLeft.y) * mult - (points.bottomRight.y - points.topRight.y) * mult,
			)

		val newBottomRight =
			Point(
				points.bottomRight.x + (points.bottomRight.x - points.topRight.x) * mult + (points.bottomRight.x - points.bottomLeft.x) * mult,
				points.bottomRight.y + (points.bottomRight.y - points.topRight.y) * mult + (points.bottomRight.y - points.bottomLeft.y) * mult,
			)

		val newBottomLeft =
			Point(
				points.bottomLeft.x - (points.bottomRight.x - points.bottomLeft.x) * mult + (points.bottomRight.x - points.topRight.x) * mult,
				points.bottomLeft.y - (points.bottomRight.y - points.bottomLeft.y) * mult + (points.bottomRight.y - points.topRight.y) * mult,
			)

		val newPoints = CornerPoints(newTopLeft, newTopRight, newBottomRight, newBottomLeft)

		// Calculate aspect ratio
		val topWidth = sqrt((newPoints.topRight.x - newPoints.topLeft.x).pow(2) + (newPoints.topRight.y - newPoints.topLeft.y).pow(2))
		val bottomWidth =
			sqrt((newPoints.bottomRight.x - newPoints.bottomLeft.x).pow(2) + (newPoints.bottomRight.y - newPoints.bottomLeft.y).pow(2))
		val maxWidth = maxOf(topWidth, bottomWidth)

		val leftHeight = sqrt((newPoints.bottomLeft.x - newPoints.topLeft.x).pow(2) + (newPoints.bottomLeft.y - newPoints.topLeft.y).pow(2))
		val rightHeight = sqrt((newPoints.bottomRight.x - newPoints.topRight.x).pow(2) + (newPoints.bottomRight.y - newPoints.topRight.y).pow(2))
		val maxHeight = maxOf(leftHeight, rightHeight)

		val aspectRatio = maxWidth / maxHeight

		// Calculate new image size
		val width = 540
		val height = (width / aspectRatio).toInt()

		// Create source and destination matrix
		val srcMatrix = MatOfPoint2f(newPoints.topLeft, newPoints.topRight, newPoints.bottomRight, newPoints.bottomLeft)
		val dstMatrix = MatOfPoint2f(Point(0.0, 0.0), Point(width - 1.0, 0.0), Point(width - 1.0, height - 1.0), Point(0.0, height - 1.0))

		// Get perspective transform matrix
		val transformMatrix = getPerspectiveTransform(srcMatrix, dstMatrix)

		// Warp image
		val result = Mat(height, width, CvType.CV_8UC1)
		warpPerspective(img, result, transformMatrix, result.size())

		return result
	}

	/**
	 * Get corner ID near enough to the point
	 *
	 * @param x: point to be checked
	 * @param height, width: image size
	 * @param limX: limit in pixel
	 * @param limY: limit in pixel
	 * @param limFrac: limit in fraction [0..1] (not used if lim defined)
	 *
	 * @return corner id (0..3) if point near corner
	 * else return -1
	 *
	 */
	private fun nearWhichCorner(
		x: Int,
		y: Int,
		height: Int,
		width: Int,
		limX: Int? = null,
		limY: Int? = null,
		limFrac: Float = 0.1F,
	): Int {
		// process limit
		val limitX: Int = limX ?: (limFrac / 2 * height).toInt()
		val limitY: Int = limY ?: (limFrac / 2 * width).toInt()

		return when {
			// Upper left 	(ID = 0)
			x <= limitX && y <= limitY -> UPPER_LEFT
			// Upper right 	(ID = 1)
			x <= limitX && y >= width - limitY -> UPPER_RIGHT
			// Lower right 	(ID = 2)
			x >= height - limitX && y >= width - limitY -> LOWER_RIGHT
			// Lower left 	(ID = 3)
			x >= height - limitX && y <= limitY -> LOWER_LEFT
			// Not in corner
			else -> -1
		}
	}

	/**
	 * Apply local normalization to an image
	 * source: https://stackoverflow.com/questions/43240604/python-local-normalization-in-opencv
	 * @see - https://bigwww.epfl.ch/demo/ip/demos/local-normalization/
	 *
	 * @param img input matrix
	 * @return local normalized img
	 */
	private fun localNormalize(img: Mat): Mat {
		// convert img to CV_32F
		val gray = Mat()
		img.convertTo(gray, CvType.CV_32F, 1.0 / 255.0)

		val blur = Mat()
		Imgproc.GaussianBlur(gray, blur, Size(0.0, 0.0), 2.0, 2.0)

		val num = Mat()
		Core.subtract(gray, blur, num)

		val numSquared = Mat()
		Core.multiply(num, num, numSquared)
		val blur2 = Mat()
		Imgproc.GaussianBlur(numSquared, blur2, Size(0.0, 0.0), 20.0, 20.0)

		val den = Mat()
		Core.sqrt(blur2, den)

		val div = Mat()
		Core.divide(num, den, div)

		Core.normalize(div, div, 0.0, 1.0, Core.NORM_MINMAX)

		// Convert back to uint8
		val result = Mat()
		div.convertTo(result, CvType.CV_8U, 255.0)

		return result
	}
}
