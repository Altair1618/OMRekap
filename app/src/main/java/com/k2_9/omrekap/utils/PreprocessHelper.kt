package com.k2_9.omrekap.utils

import android.graphics.Bitmap
import com.k2_9.omrekap.data.models.CornerPoints
import com.k2_9.omrekap.data.models.ImageSaveData
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

object PreprocessHelper {
	private const val FINAL_WIDTH = 540.0
	private const val FINAL_HEIGHT = 960.0

	fun preprocessImage(data: ImageSaveData): ImageSaveData {
		// Initialize Mats
		val mainImageMat = Mat()
		val annotatedImageMat = Mat()

		Utils.bitmapToMat(data.rawImage, mainImageMat)
		Utils.bitmapToMat(data.annotatedImage, annotatedImageMat)

		// Preprocess both images
		var mainImageResult = preprocessMat(mainImageMat)
		var annotatedImageResult = preprocessMat(annotatedImageMat)

		// Get corner points
		val cornerPoints = CropHelper.detectCorner(mainImageResult)

		// Crop both images
		mainImageResult = CropHelper.fourPointTransform(mainImageResult, cornerPoints)
		annotatedImageResult = CropHelper.fourPointTransform(annotatedImageResult, cornerPoints)

		// Annotate annotated image
		// TODO: Call function to annotate image
		 annotatedImageResult = ImageAnnotationHelper.annotateCorner(annotatedImageResult, cornerPoints)

		// Re-resize both images
		mainImageResult = resizeMat(mainImageResult)
		annotatedImageResult = resizeMat(annotatedImageResult)

		// Convert Mats to Bitmaps
		val mainImageBitmap = Bitmap.createBitmap(mainImageResult.width(), mainImageResult.height(), Bitmap.Config.ARGB_8888)
		val annotatedImageBitmap = Bitmap.createBitmap(annotatedImageResult.width(), annotatedImageResult.height(), Bitmap.Config.ARGB_8888)

		Utils.matToBitmap(mainImageResult, mainImageBitmap)
		Utils.matToBitmap(annotatedImageResult, annotatedImageBitmap)

		return ImageSaveData(mainImageBitmap, annotatedImageBitmap, data.data)
	}

	private fun preprocessMat(img: Mat): Mat {
		return img.apply {
			resizeMat(this)
			normalize(this)
		}
	}

	fun preprocessPattern(img: Mat): Mat {
		return img.apply {
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
