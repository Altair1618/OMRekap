package com.k2_9.omrekap.utils

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

object PreprocessHelper {
	const val FINAL_WIDTH = 540
	const val FINAL_HEIGHT = 960

	fun preprocessImage(img: Bitmap): Bitmap {
		val mat = Mat()
		Utils.bitmapToMat(img, mat)
		val resultMat = preprocessImage(mat)
		val resultBitmap = Bitmap.createBitmap(resultMat.width(), resultMat.height(), Bitmap.Config.ARGB_8888)
		Utils.matToBitmap(resultMat, resultBitmap)
		return resultBitmap
	}

	fun preprocessImage(img: Mat): Mat {
		return img.apply {
			resizeMat(this, FINAL_WIDTH.toDouble(), FINAL_HEIGHT.toDouble())
			normalize(this)
		}
	}

	fun resizeMat(
		img: Mat,
		width: Double,
		height: Double,
	): Mat {
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
