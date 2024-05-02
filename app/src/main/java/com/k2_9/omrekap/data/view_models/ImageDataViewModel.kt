package com.k2_9.omrekap.data.view_models

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k2_9.omrekap.data.configs.omr.CircleTemplateLoader
import com.k2_9.omrekap.data.configs.omr.OMRSection
import com.k2_9.omrekap.data.models.ImageSaveData
import com.k2_9.omrekap.utils.AprilTagHelper
import com.k2_9.omrekap.utils.ImageAnnotationHelper
import com.k2_9.omrekap.utils.omr.ContourOMRHelper
import com.k2_9.omrekap.utils.omr.OMRConfigDetector
import com.k2_9.omrekap.utils.omr.OMRHelper
import com.k2_9.omrekap.utils.omr.TemplateMatchingOMRHelper
import kotlinx.coroutines.launch
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.time.Instant

class ImageDataViewModel : ViewModel() {
	private val _data = MutableLiveData<ImageSaveData?>()
	val data = _data as LiveData<ImageSaveData?>

	fun processImage(
		data: ImageSaveData,
		circleTemplateLoader: CircleTemplateLoader,
	) {
		viewModelScope.launch {
			val rawImage = data.rawImage
			val imageMat = Mat()
// 			val annotatedImageMat = Mat()
			Utils.bitmapToMat(rawImage, imageMat)

			// convert image to gray
			val grayImageMat = Mat()
			Imgproc.cvtColor(imageMat, grayImageMat, Imgproc.COLOR_BGR2GRAY)

			// load configuration
			val configurationResult = OMRConfigDetector.detectConfiguration(grayImageMat)

			if (configurationResult == null) {
				_data.value = data
				return@launch
			}

			val (loadedConfig, _, _) = configurationResult

			// annotate the detected AprilTag
			var annotatedImage = AprilTagHelper.annotateImage(rawImage)

			// process OMR
			val matImage = Mat()

			Utils.bitmapToMat(data.rawImage, matImage)

			loadedConfig.contourOMRHelperConfig.omrCropper.config.setImage(matImage)
			loadedConfig.templateMatchingOMRHelperConfig.omrCropper.config.setImage(matImage)
			loadedConfig.templateMatchingOMRHelperConfig.setTemplate(circleTemplateLoader)

			val contourOMRHelper = ContourOMRHelper(loadedConfig.contourOMRHelperConfig)
			val templateMatchingOMRHelper = TemplateMatchingOMRHelper(loadedConfig.templateMatchingOMRHelperConfig)

			val result: MutableMap<OMRSection, Int?> = mutableMapOf()

			for (section in OMRSection.entries) {
				try {
					result[section] = contourOMRHelper.detect(section)
				} catch (e: OMRHelper.DetectionError) {
					try {
						result[section] = templateMatchingOMRHelper.detect(section)
					} catch (e: OMRHelper.DetectionError) {
						result[section] = null
					}
				}
			}

			// TODO: move this to april tag
			val customMap = mutableMapOf<OMRSection, String>()

			customMap[OMRSection.FIRST] = "Anis"
			customMap[OMRSection.SECOND] = "Bowo"
			customMap[OMRSection.THIRD] = "Janggar"

			val stringKeyResult = mutableMapOf<String, Int?>()

			result.let {
				for ((section, value) in it) {
					stringKeyResult[customMap[section]!!] = value

					annotatedImage = ImageAnnotationHelper.annotateOMR(annotatedImage, contourOMRHelper.getSectionPosition(section), value)
					Log.d("Result", "${customMap[section]}: $value")
				}
			}

			data.data = stringKeyResult
			data.timestamp = Instant.now()

			// TODO: annotate omr result
			val annotatedImageBitmap =
				Bitmap.createBitmap(
					annotatedImage.width(),
					annotatedImage.height(),
					Bitmap.Config.ARGB_8888,
				)
			Utils.matToBitmap(annotatedImage, annotatedImageBitmap)
			data.annotatedImage = annotatedImageBitmap
			_data.value = data
		}
	}

	fun resetState() {
		_data.value = null
	}
}
