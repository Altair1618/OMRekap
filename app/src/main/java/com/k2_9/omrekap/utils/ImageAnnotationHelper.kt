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
		// Change image to color
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
			val originalPoint = points[0][0]
			val drawnPoint = Point(originalPoint.x - 30, originalPoint.y - 40.0)

			Imgproc.putText(
				imgWithAnnotations,
				id,
				drawnPoint,
				Imgproc.FONT_HERSHEY_SIMPLEX,
				0.5,
				Scalar(0.0, 255.0, 0.0),
				1,
			)
			Imgproc.polylines(
				imgWithAnnotations,
				listOf(MatOfPoint(*points[0].toTypedArray())),
				true,
				Scalar(0.0, 255.0, 0.0),
				1,
			)
		} else {
			val topLeft = Point(cornerPoints[0].get(0, 0)[0], cornerPoints[0].get(1, 0)[0])
			Imgproc.putText(
				imgWithAnnotations,
				"Not Detected",
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
		// Change image to color
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

	fun annotateOMR(
		img: Mat,
		section: Rect,
		result: Int?,
	): Mat {
		val imgWithAnnotations = img.clone()

		// Draw text on the image
		if (result == null) {
			Imgproc.putText(
				imgWithAnnotations,
				"Not Detected",
				Point(section.x.toDouble() - 13.0, section.y.toDouble() - 20.0),
				Imgproc.FONT_HERSHEY_SIMPLEX,
				0.5,
				Scalar(0.0, 255.0, 0.0),
				1,
			)
		} else {
			Imgproc.putText(
				imgWithAnnotations,
				"$result",
				Point(section.x.toDouble() + 50.0, section.y.toDouble() - 10.0),
				Imgproc.FONT_HERSHEY_SIMPLEX,
				1.0,
				Scalar(0.0, 255.0, 0.0),
				2,
			)
		}
		Imgproc.rectangle(
			imgWithAnnotations,
			section.tl(),
			section.br(),
			Scalar(0.0, 255.0, 0.0),
			2,
		)
		return imgWithAnnotations
	}
}
