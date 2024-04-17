package com.k2_9.omrekap.utils

import android.graphics.Bitmap
import com.k2_9.omrekap.data.models.CornerPoints
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

object ImageAnnotationHelper {
	fun annotateCorner(img: Bitmap, cornerPoints: CornerPoints): Bitmap {
		val imgMat = Mat()
		Utils.bitmapToMat(img, imgMat)
		Imgproc.circle(imgMat,cornerPoints.topLeft, 10, Scalar(0.0, 255.0, 0.0), 5)
		Imgproc.circle(imgMat,cornerPoints.topRight, 10, Scalar(0.0, 255.0, 0.0), 5)
		Imgproc.circle(imgMat,cornerPoints.bottomLeft, 10, Scalar(0.0, 255.0, 0.0), 5)
		Imgproc.circle(imgMat,cornerPoints.bottomRight, 10, Scalar(0.0, 255.0, 0.0), 5)
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
