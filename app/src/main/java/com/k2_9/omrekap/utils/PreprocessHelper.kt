package com.k2_9.omrekap.utils

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class PreprocessHelper {
	companion object {
		const val FINAL_WIDTH = 540
		const val FINAL_HEIGHT = 960
	}
	fun preprocess(img: Bitmap): Bitmap {
		val mat = Mat()
		Utils.bitmapToMat(img, mat)
		val resultMat = preprocess(mat)
		val resultBitmap = Bitmap.createBitmap(resultMat.width(), resultMat.height(), Bitmap.Config.ARGB_8888)
		Utils.matToBitmap(resultMat, resultBitmap)
		return resultBitmap
	}
	fun preprocess(img: Mat): Mat {
		return img.apply {
			resize(this, FINAL_WIDTH.toDouble(), FINAL_HEIGHT.toDouble())
			normalize(this)
		}
	}
	fun resize(img: Mat, width: Double, height: Double): Mat {
		val resizedImg = Mat()
		Imgproc.resize(img, resizedImg, Size(width, height))
		return resizedImg
	}
	fun normalize(img: Mat): Mat {
		val normalizedImg = Mat()
		Core.normalize(img, normalizedImg)
		return normalizedImg
	}
}
