package com.k2_9.omrekap.utils

import com.k2_9.omrekap.data.models.CornerPoints
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

object ImageAnnotationHelper {
	fun annotateCorner(
		img: Mat,
		cornerPoints: CornerPoints,
	): Mat {
		val imgWithAnnotations = img.clone()
		Imgproc.circle(imgWithAnnotations, cornerPoints.topLeft, 10, Scalar(0.0, 255.0, 0.0), 5)
		Imgproc.circle(imgWithAnnotations, cornerPoints.topRight, 10, Scalar(0.0, 255.0, 0.0), 5)
		Imgproc.circle(imgWithAnnotations, cornerPoints.bottomLeft, 10, Scalar(0.0, 255.0, 0.0), 5)
		Imgproc.circle(imgWithAnnotations, cornerPoints.bottomRight, 10, Scalar(0.0, 255.0, 0.0), 5)
		return imgWithAnnotations
	}

	fun annotateAprilTag(
		img: Mat,
		cornerPoints: List<Mat>,
		id: String,
	): Mat {
		val imgWithAnnotations = img.clone()
		/* Change image to color */
		Imgproc.cvtColor(img, imgWithAnnotations, Imgproc.COLOR_GRAY2BGR)
		if (id.isNotEmpty()) {
			// points -> list<Point*s*>, inside list of points are corners of the detector
			val points =
				cornerPoints.map { mat ->
					val points = ArrayList<Point>()
					for (i in 0..<4) {
						val x = mat.get(0, i)[0]
						val y = mat.get(0, i)[1]
						points.add(Point(x, y))
					}
					points
				}

			// Draw ID and bounding box
			Imgproc.putText(
				imgWithAnnotations,
				id,
				points[0][0],
				Imgproc.FONT_HERSHEY_SIMPLEX,
				0.5,
				Scalar(0.0, 255.0, 0.0),
				1
			)
			Imgproc.polylines(
				imgWithAnnotations,
				listOf(MatOfPoint(*points[0].toTypedArray())),
				true,
				Scalar(0.0, 255.0, 0.0),
				1
			)
		} else {
			val topLeft = Point(cornerPoints[0].get(0, 0)[0], cornerPoints[0].get(1, 0)[0])
			Imgproc.putText(
				imgWithAnnotations,
				"April Tag Not Detected",
				topLeft,
				Imgproc.FONT_HERSHEY_SIMPLEX,
				1.0,
				Scalar(0.0, 255.0, 0.0),
				5,
			)
		}
		return imgWithAnnotations
	}

	fun annotateTemplateMatchingOMR(
		img: Mat,
		cornerPoints: List<Rect>,
		contourNumber: Int,
	): Mat {
		val imgWithAnnotations = img.clone()
		/* Change image to color */
		Imgproc.cvtColor(img, imgWithAnnotations, Imgproc.COLOR_GRAY2BGR)
		for (rect in cornerPoints) {
			Imgproc.rectangle(imgWithAnnotations, rect.tl(), rect.br(), Scalar(0.0, 255.0, 0.0), 1)
		}
		Imgproc.putText(
			imgWithAnnotations,
			"$contourNumber",
			cornerPoints[0].tl(),
			Imgproc.FONT_HERSHEY_SIMPLEX,
			0.5,
			Scalar(0.0, 255.0, 0.0),
			1,
		)
		return imgWithAnnotations
	}

	fun annotateContourOMR(
		img: Mat,
		cornerPoints: List<MatOfPoint>,
		contourNumber: Int,
	): Mat {
		val imgWithAnnotations = img.clone()
		Imgproc.cvtColor(img, imgWithAnnotations, Imgproc.COLOR_GRAY2BGR)
		for (contour in cornerPoints) {
			val rect = Imgproc.boundingRect(contour)
			Imgproc.rectangle(imgWithAnnotations, rect.tl(), rect.br(), Scalar(0.0, 255.0, 0.0), 1)
		}
		Imgproc.putText(
			imgWithAnnotations,
			"$contourNumber",
			Point(0.0, 20.0),
			Imgproc.FONT_HERSHEY_SIMPLEX,
			0.5,
			Scalar(0.0, 255.0, 0.0),
			1,
		)
		return imgWithAnnotations
	}

	fun annotateOMR(img: Mat, cornerPoints: MatOfPoint, result: Int ): Mat {
		val imgWithAnnotations = img.clone()
		Imgproc.cvtColor(img, imgWithAnnotations, Imgproc.COLOR_GRAY2BGR)
		Imgproc.putText(imgWithAnnotations, "$result", cornerPoints.toList()[0], Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, Scalar(0.0, 255.0, 0.0), 1)
		return imgWithAnnotations
	}
}
