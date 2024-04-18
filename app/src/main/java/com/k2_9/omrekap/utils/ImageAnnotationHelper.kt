package com.k2_9.omrekap.utils

import com.k2_9.omrekap.data.models.CornerPoints
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

object ImageAnnotationHelper {
	fun annotateCorner(
		img: Mat,
		cornerPoints: CornerPoints,
	): Mat {
		Imgproc.circle(img, cornerPoints.topLeft, 10, Scalar(0.0, 255.0, 0.0), 5)
		Imgproc.circle(img, cornerPoints.topRight, 10, Scalar(0.0, 255.0, 0.0), 5)
		Imgproc.circle(img, cornerPoints.bottomLeft, 10, Scalar(0.0, 255.0, 0.0), 5)
		Imgproc.circle(img, cornerPoints.bottomRight, 10, Scalar(0.0, 255.0, 0.0), 5)
		return img
	}

	fun annotateAprilTag(
		img: Mat,
		cornerPoints: List<Mat>,
		id: String,
	): Mat {
		val imgWithAnnotations = img.clone()
		if (id.isNotEmpty()) {
			val points =
				cornerPoints.map { mat ->
					val x = mat.get(0, 0)[0]
					val y = mat.get(1, 0)[0]
					Point(x, y)
				}

			// Draw ID and bounding box
			Imgproc.putText(imgWithAnnotations, id, points[0], Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, Scalar(0.0, 255.0, 0.0), 5)
			Imgproc.polylines(imgWithAnnotations, listOf(MatOfPoint(*points.toTypedArray())), true, Scalar(0.0, 255.0, 0.0), 5)
		} else {
			val topLeft = Point(cornerPoints[0].get(0, 0)[0], cornerPoints[0].get(1, 0)[0])
			Imgproc.putText(imgWithAnnotations, "April Tag Not Detected", topLeft, Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, Scalar(0.0, 255.0, 0.0), 5)
		}
		return imgWithAnnotations
	}

	fun annotateOMR()  {
		TODO()
	}
}
