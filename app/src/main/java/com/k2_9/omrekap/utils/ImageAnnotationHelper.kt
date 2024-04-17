package com.k2_9.omrekap.utils

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class ImageAnnotationHelper {
	fun annotateCorner(img: Bitmap, cornerPoints: List<MatOfPoint>): Bitmap {
		val imgMat = Mat()
		Utils.bitmapToMat(img, imgMat)
		cornerPoints.forEach{
			Imgproc.polylines(imgMat, it, true, Scalar(255.0, 0.0, 0.0), 5)

		}
		val annotatedImg = Bitmap.createBitmap(imgMat.width(), imgMat.height(), Bitmap.Config.ARGB_8888)
		Utils.matToBitmap(imgMat, annotatedImg)
		return annotatedImg
	}

	fun annotateAprilTag(){
		TODO()
	}

	fun annotateOMR(){
		TODO()
	}
}
