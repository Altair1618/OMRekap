package com.k2_9.omrekap.utils

import android.graphics.Bitmap
import com.k2_9.omrekap.data.models.CornerPoints
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

object PreprocessHelper {
	private const val FINAL_WIDTH = 540.0
	private const val FINAL_HEIGHT = 960.0

	fun preprocessImage(img: Bitmap): Bitmap {
		val mat = Mat()
		Utils.bitmapToMat(img, mat)
		val resultMat = preprocessImage(mat)
		var resultBitmap = Bitmap.createBitmap(resultMat.width(), resultMat.height(), Bitmap.Config.ARGB_8888)
		Utils.matToBitmap(resultMat, resultBitmap)

		val corners = CropHelper.detectCorner(resultBitmap)
		resultBitmap = CropHelper.fourPointTransform(resultBitmap, corners)

		return resultBitmap
	}

	private fun preprocessImage(img: Mat): Mat {
		return img.apply {
			resizeMat(this)
			normalize(this)
		}
	}

	private fun resizeMat(img: Mat): Mat {
		val resizedImg = Mat()
		Imgproc.resize(img, resizedImg, Size(FINAL_WIDTH, FINAL_HEIGHT))
		return resizedImg
	}
	private fun normalize(img: Mat): Mat {
		val normalizedImg = Mat()
		Core.normalize(img, normalizedImg)
		return normalizedImg
	}
}
