package com.k2_9.omrekap.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.k2_9.omrekap.R
import com.k2_9.omrekap.data.models.CornerPoints
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.getPerspectiveTransform
import org.opencv.imgproc.Imgproc.warpPerspective
import kotlin.math.pow
import kotlin.math.sqrt

object CropHelper {
	private const val UPPER_LEFT: Int = 0
	private const val UPPER_RIGHT: Int = 1
	private const val LOWER_RIGHT: Int = 2
	private const val LOWER_LEFT: Int = 3

	fun detectCorner(image: Bitmap, caller: Context): CornerPoints {
		// Convert to Matrix (Mat)
		val imageMatrix = Mat(image.height, image.width, CvType.CV_8UC1)
		Utils.bitmapToMat(image, imageMatrix)

		// Find corner pattern
		val options = BitmapFactory.Options()
		options.inPreferredConfig = Bitmap.Config.ARGB_8888
		val cornerPatternBitmap: Bitmap
			= BitmapFactory.decodeResource(caller.resources, R.raw.corner_pattern, options)
		val cornerPatternMatrix = Mat(cornerPatternBitmap.height, cornerPatternBitmap.width, CvType.CV_8UC1)
		Utils.bitmapToMat(cornerPatternBitmap, cornerPatternMatrix)

		val resultMatrix = Mat(
			image.height - cornerPatternBitmap.height + 1,
			image.width - cornerPatternBitmap.width + 1,
			CvType.CV_8UC1
		)
		Imgproc.matchTemplate(imageMatrix, cornerPatternMatrix, resultMatrix, Imgproc.TM_SQDIFF_NORMED)
		var upperLeftPoint = Point()
		var upperRightPoint = Point()
		var lowerLeftPoint = Point()
		var lowerRightPoint = Point()

		val needed = mutableListOf(true, true, true, true)
		var needChange = 4

		data class PointsAndWeight (
			val x: Int,
			val y: Int,
			val weight: Double
		)

		val pointsList: MutableList<PointsAndWeight> = mutableListOf()

		for (i in 0..<resultMatrix.height() step 4) {
			for (j in 0..<resultMatrix.width() step 4) {
				pointsList.add(PointsAndWeight(i, j, resultMatrix.get(i, j)[0]))
			}
		}

		pointsList.sortBy { it.weight }

		pointsList.forEach {
			if (needChange == 0) {
				return@forEach
			}
			val corner = nearWhichCorner(it.x, it.y, resultMatrix.height(), resultMatrix.width(), limFrac = 0.1F)
			if (corner == -1) {
				return@forEach
			}
			if (needed[corner]) {
				needed[corner] = false
				needChange--
				val pointFromIt = Point(it.x.toDouble(), it.y.toDouble())
				when (corner) {
					UPPER_LEFT -> upperLeftPoint = pointFromIt
					UPPER_RIGHT -> upperRightPoint = pointFromIt
					LOWER_RIGHT -> lowerRightPoint = pointFromIt
					LOWER_LEFT -> lowerLeftPoint = pointFromIt
				}
			}
		}


		Log.d("Corner", "type = ${resultMatrix.type()}, should be ${CvType.CV_8UC1}")

		if (needChange > 0) {
			throw Exception("Not all corner points found!")
		}
		return CornerPoints(upperLeftPoint, upperRightPoint, lowerRightPoint, lowerLeftPoint)
	}

	fun fourPointTransform(
        image: Bitmap,
        points: CornerPoints,
	): Bitmap {
		val mult = 0.03

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

		val topWidth =
			sqrt(
				(newPoints.topRight.x - newPoints.topLeft.x).pow(2) +
					(newPoints.topRight.y - newPoints.topLeft.y).pow(2),
			)

		val bottomWidth =
			sqrt(
				(newPoints.bottomRight.x - newPoints.bottomLeft.x).pow(2) +
					(newPoints.bottomRight.y - newPoints.bottomLeft.y).pow(2),
			)

		val maxWidth = maxOf(topWidth, bottomWidth)

		val leftHeight =
			sqrt(
				(newPoints.bottomLeft.x - newPoints.topLeft.x).pow(2) +
					(newPoints.bottomLeft.y - newPoints.topLeft.y).pow(2),
			)

		val rightHeight =
			sqrt(
				(newPoints.bottomRight.x - newPoints.topRight.x).pow(2) +
					(newPoints.bottomRight.y - newPoints.topRight.y).pow(2),
			)

		val maxHeight = maxOf(leftHeight, rightHeight)

		val aspectRatio = maxWidth / maxHeight

		val width = 800
		val height = (width / aspectRatio).toInt()

		val srcMatrix =
			MatOfPoint2f(
				newPoints.topLeft,
				newPoints.topRight,
				newPoints.bottomRight,
				newPoints.bottomLeft,
			)

		val dstMatrix =
			MatOfPoint2f(
				Point(0.0, 0.0),
				Point(width - 1.0, 0.0),
				Point(0.0, height - 1.0),
				Point(width - 1.0, height - 1.0),
			)

		val transformMatrix = getPerspectiveTransform(srcMatrix, dstMatrix)

		val imageMatrix = Mat(image.height, image.width, CvType.CV_8UC1)
		Utils.bitmapToMat(image, imageMatrix)

		val resultMatrix = Mat(height, width, CvType.CV_8UC1)
		warpPerspective(imageMatrix, resultMatrix, transformMatrix, resultMatrix.size())

		val bitmapResult: Bitmap = Bitmap.createBitmap(resultMatrix.width(), resultMatrix.height(), Bitmap.Config.ARGB_8888)
		Utils.matToBitmap(resultMatrix, bitmapResult)
		return bitmapResult
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
	private fun nearWhichCorner(x: Int, y: Int,
								height: Int, width: Int,
								limX: Int? = null,
								limY: Int? = null,
								limFrac: Float = 0.1F): Int {
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
}
