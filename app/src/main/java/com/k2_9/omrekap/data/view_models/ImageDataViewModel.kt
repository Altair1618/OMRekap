package com.k2_9.omrekap.data.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k2_9.omrekap.data.models.ImageSaveData
import com.k2_9.omrekap.utils.AprilTagHelper
import com.k2_9.omrekap.utils.omr.OMRConfigDetector
import kotlinx.coroutines.launch
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class ImageDataViewModel : ViewModel() {
	private val _data = MutableLiveData<ImageSaveData>()
	val data = _data as LiveData<ImageSaveData>

	fun processImage(data: ImageSaveData) {
		viewModelScope.launch {
			val rawImage = data.rawImage
			val imageMat = Mat()
//			val annotatedImageMat = Mat()
			Utils.bitmapToMat(rawImage, imageMat)

			// convert image to gray
			val grayImageMat = Mat()
			Imgproc.cvtColor(imageMat, grayImageMat, Imgproc.COLOR_BGR2GRAY)

			// load configuration
			val (loadedConfig, id, corners) = OMRConfigDetector.detectConfiguration(grayImageMat)!!

			// annotate the detected AprilTag
			val annotatedImage = AprilTagHelper.annotateImage(rawImage)

			// TODO: Process the raw image using OMRHelper
			data.annotatedImage = annotatedImage
			_data.value = data
		}
	}
}
