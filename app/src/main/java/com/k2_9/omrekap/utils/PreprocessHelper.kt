package com.k2_9.omrekap.utils

import android.graphics.Bitmap
import com.k2_9.omrekap.data.models.ImageSaveData
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.time.Instant

object PreprocessHelper {
	private const val FINAL_WIDTH = 900.0
	private const val FINAL_HEIGHT = 1600.0

	/**
	 * Uses OpenCV module, remember OpenCVLoader.initLocal() has been run before
	 *
	 * Initialize corner pattern first using [CropHelper.loadPattern]
	 *
	 * Preprocess image from photo result / gallery until it is cropped and ready for OMR
	 * @param data Viewmodel containing raw and annotated image (before this method they are the same)
	 *
	 * @return new ImageSaveData viewmodel with cropped image
	 */
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

		// Annotate annotated image
		annotatedImageResult = ImageAnnotationHelper.annotateCorner(annotatedImageResult, cornerPoints)

		// Crop both images
		mainImageResult = CropHelper.fourPointTransform(mainImageResult, cornerPoints)
		annotatedImageResult = CropHelper.fourPointTransform(annotatedImageResult, cornerPoints)

		// Re-resize both images
		mainImageResult = resizeMat(mainImageResult)
		annotatedImageResult = resizeMat(annotatedImageResult)

		// Convert Mats to Bitmaps
		val mainImageBitmap = Bitmap.createBitmap(mainImageResult.width(), mainImageResult.height(), Bitmap.Config.ARGB_8888)
		val annotatedImageBitmap = Bitmap.createBitmap(annotatedImageResult.width(), annotatedImageResult.height(), Bitmap.Config.ARGB_8888)

		Utils.matToBitmap(mainImageResult, mainImageBitmap)
		Utils.matToBitmap(annotatedImageResult, annotatedImageBitmap)

		return ImageSaveData(mainImageBitmap, annotatedImageBitmap, data.data, Instant.now())
	}

	private fun preprocessMat(img: Mat): Mat {
		return resizeMat(img)
	}

	fun preprocessPattern(img: Mat): Mat {
		return normalize(img)
	}

	private fun resizeMat(img: Mat): Mat {
		val resizedImg = Mat(Size(FINAL_WIDTH, FINAL_HEIGHT), img.type())
		Imgproc.resize(img, resizedImg, Size(FINAL_WIDTH, FINAL_HEIGHT), 0.0, 0.0, Imgproc.INTER_CUBIC)
		return resizedImg
	}

	private fun normalize(img: Mat): Mat {
		val normalizedImg = Mat()
		Core.normalize(img, normalizedImg, 0.0, 255.0, Core.NORM_MINMAX)
		return normalizedImg
	}
}
