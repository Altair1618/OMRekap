package com.k2_9.omrekap.utils

import android.graphics.Bitmap
import com.k2_9.omrekap.models.CornerPoints
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc.getPerspectiveTransform
import org.opencv.imgproc.Imgproc.warpPerspective
import kotlin.math.pow
import kotlin.math.sqrt

class CropHelper {
	fun fourPointTransform(
		image: Bitmap,
		points: CornerPoints,
	): Bitmap {
		val mult = 0.03

		val newTopLeft = Point(
			points.topLeft.x - (points.topRight.x - points.topLeft.x) * mult - (points.bottomLeft.x - points.topLeft.x) * mult,
			points.topLeft.y - (points.topRight.y - points.topLeft.y) * mult - (points.bottomLeft.y - points.topLeft.y) * mult,
		)

		val newTopRight = Point(
			points.topRight.x + (points.topRight.x - points.topLeft.x) * mult - (points.bottomRight.x - points.topRight.x) * mult,
			points.topRight.y + (points.topRight.y - points.topLeft.y) * mult - (points.bottomRight.y - points.topRight.y) * mult,
		)

		val newBottomRight = Point(
			points.bottomRight.x + (points.bottomRight.x - points.topRight.x) * mult + (points.bottomRight.x - points.bottomLeft.x) * mult,
			points.bottomRight.y + (points.bottomRight.y - points.topRight.y) * mult + (points.bottomRight.y - points.bottomLeft.y) * mult,
		)

		val newBottomLeft = Point(
			points.bottomLeft.x - (points.bottomRight.x - points.bottomLeft.x) * mult + (points.bottomRight.x - points.topRight.x) * mult,
			points.bottomLeft.y - (points.bottomRight.y - points.bottomLeft.y) * mult + (points.bottomRight.y - points.topRight.y) * mult,
		)

		val newPoints = CornerPoints(newTopLeft, newTopRight, newBottomRight, newBottomLeft)

		val topWidth = sqrt(
			(newPoints.topRight.x - newPoints.topLeft.x).pow(2) +
				(newPoints.topRight.y - newPoints.topLeft.y).pow(2)
		)

		val bottomWidth = sqrt(
			(newPoints.bottomRight.x - newPoints.bottomLeft.x).pow(2) +
				(newPoints.bottomRight.y - newPoints.bottomLeft.y).pow(2)
		)

		val maxWidth = maxOf(topWidth, bottomWidth)

		val leftHeight = sqrt(
			(newPoints.bottomLeft.x - newPoints.topLeft.x).pow(2) +
				(newPoints.bottomLeft.y - newPoints.topLeft.y).pow(2)
		)

		val rightHeight = sqrt(
			(newPoints.bottomRight.x - newPoints.topRight.x).pow(2) +
				(newPoints.bottomRight.y - newPoints.topRight.y).pow(2)
		)

		val maxHeight = maxOf(leftHeight, rightHeight)

		val aspectRatio = maxWidth / maxHeight

		val width = 800
		val height = (width / aspectRatio).toInt()

		val srcMatrix = MatOfPoint2f(
			newPoints.topLeft,
			newPoints.topRight,
			newPoints.bottomRight,
			newPoints.bottomLeft,
		)

		val dstMatrix = MatOfPoint2f(
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
}
