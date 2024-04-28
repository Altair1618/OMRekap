package com.k2_9.omrekap.utils

import android.graphics.Bitmap
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.ArucoDetector
import org.opencv.objdetect.DetectorParameters
import org.opencv.objdetect.Dictionary
import org.opencv.objdetect.Objdetect

private const val LOG_TAG = "AprilTagHelper"

object AprilTagHelper {
	/**
	 * Uses OpenCV module, remember OpenCVLoader.initLocal() has been run before
	 *
	 * @param imageBitmap image with tags to be detected in Bitmap
	 *
	 * @return List of possible IDs detected in the image,
	 * returns empty list if no valid tag is found
	 */
	fun getAprilTagId(imageBitmap: Bitmap): Pair<List<String>, List<Mat>> {
		val grayImageMat: Mat = prepareImage(imageBitmap)
		return getAprilTagId(grayImageMat)
	}

	/**
	 * Uses OpenCV module, remember OpenCVLoader.initLocal() has been run before
	 *
	 * @param imageMat image with tags to be detected in OpenCV's Mat type.
	 * Image has to be in grayscale to be able to be processed
	 *
	 * @return List of possible IDs detected in the image,
	 * returns empty list if no valid tag is found
	 */

	fun getAprilTagId(imageMat: Mat): Pair<List<String>, List<Mat>> {
		// TODO refactor to singleton pattern if initiation behavior is well known
		// TODO refactor AprilTag family to be read from config file
		val detector: ArucoDetector =
			prepareDetector(
				Objdetect.getPredefinedDictionary(Objdetect.DICT_APRILTAG_36h10),
			)

		// prepare output data containers
		val corners: MutableList<Mat> = ArrayList()
		val idMat = Mat()
		// added in case needed in the future or for debugging purpose
		val rejectedCandidates: MutableList<Mat> = ArrayList()

		// perform detection
		detector.detectMarkers(imageMat, corners, idMat, rejectedCandidates)

		logDebug("found ${rejectedCandidates.size} possible tags")
		// get IDs from OpenCV's Mat
		val idList: MutableList<String> = ArrayList()
		val nId: Int = idMat.size().height.toInt()
		logDebug("found $nId IDs")
		for (i in 0..<nId) {
			val id = idMat[i, 0][0].toInt().toString()
			logDebug("detected tag with id: $id")
			val cornerPoints = corners[i]
			logDebug(
				"with corners at: (${cornerPoints[0, 0][0]},${cornerPoints[0, 0][1]}), " +
					"(${cornerPoints[0, 1][0]},${cornerPoints[0, 1][1]}) " +
					"(${cornerPoints[0, 2][0]},${cornerPoints[0, 2][1]}) " +
					"(${cornerPoints[0, 3][0]},${cornerPoints[0, 3][1]})",
			)
			idList.add(id)
		}

		return (idList to corners)
	}

	fun annotateImage(imageBitmap: Bitmap): Bitmap {
		val res = getAprilTagId(imageBitmap)
		val cornerPoints = res.second
		val ids = (res.first)[0]
		val annotatedImageMat =
			ImageAnnotationHelper.annotateAprilTag(prepareImage(imageBitmap), cornerPoints, ids)
		val annotatedImageBitmap =
			Bitmap.createBitmap(
				annotatedImageMat.width(),
				annotatedImageMat.height(),
				Bitmap.Config.ARGB_8888,
			)
		Utils.matToBitmap(annotatedImageMat, annotatedImageBitmap)
		return annotatedImageBitmap
	}

	private fun prepareDetector(detectorDictionary: Dictionary): ArucoDetector {
		// initialize detector parameters
		val detectorParameters = DetectorParameters()
		// instantiate and return detector
		return ArucoDetector(detectorDictionary, detectorParameters)
	}

	private fun prepareImage(imageBitmap: Bitmap): Mat {
		// transform to OpenCV Mat data
		val imageMat = Mat()
		val grayImageMat = Mat()
		Utils.bitmapToMat(imageBitmap, imageMat)
		// transform to grayscale for ArucoDetector
		Imgproc.cvtColor(imageMat, grayImageMat, Imgproc.COLOR_BGR2GRAY)

		return grayImageMat
	}

	private fun logDebug(msg: String) {
		Log.d(LOG_TAG, msg)
	}
}
