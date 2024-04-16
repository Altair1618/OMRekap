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
	suspend fun getAprilTagId(imageBitmap: Bitmap): List<String> {
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

	suspend fun getAprilTagId(imageMat: Mat): List<String> {
		// TODO refactor to singleton pattern if initiation behavior is well known
		// TODO refactor AprilTag family to be read from config file
		val detector: ArucoDetector =
			prepareDetector(
				Objdetect.getPredefinedDictionary(Objdetect.DICT_APRILTAG_36h10),
			)

		// prepare output data containers
		val corners: List<Mat> = ArrayList()
		val idMat = Mat()
		// added in case needed in the future or for debugging purpose
		val rejectedCandidates: List<Mat> = ArrayList()

		// perform detection
		detector.detectMarkers(imageMat, corners, idMat, rejectedCandidates)

		logDebug("found ${rejectedCandidates.size} possible tags")
		// get IDs from OpenCV's Mat
		val idList: MutableList<String> = ArrayList()
		val nId: Int = idMat.size().height.toInt()
		logDebug("found $nId IDs")
		for (i in 0..<nId) {
			val id = idMat[i, 0][0].toInt()
			logDebug("detected tag with id: $id")
			idList += id.toString()
		}

		return idList
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
