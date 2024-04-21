package com.k2_9.omrekap.aprilTag

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.k2_9.omrekap.R
import com.k2_9.omrekap.data.repository.OMRConfigRepository
import com.k2_9.omrekap.utils.omr.OMRConfigurationDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.runner.RunWith
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

@RunWith(AndroidJUnit4::class)
class AprilTagConfigDetectionTest {
	@Test
	fun test_detect() {
		val appContext = InstrumentationRegistry.getInstrumentation().targetContext
		OpenCVLoader.initLocal()

		val imageMat = Utils.loadResource(appContext, R.raw.example)

		val grayImageMat = Mat()
		// transform to grayscale for ArucoDetector
		Imgproc.cvtColor(imageMat, grayImageMat, Imgproc.COLOR_BGR2GRAY)

		CoroutineScope(Dispatchers.Default).launch {
			OMRConfigurationDetector.loadConfiguration(
				appContext
			)
			val result = OMRConfigurationDetector.detectConfiguration(grayImageMat)
			val gson = Gson()
			Log.d("ConfigDetectionTestx", gson.toJson(result))
			val compare = OMRConfigRepository.loadConfigurations(appContext)

//			val resultHash = result!!.first.hashCode()
//			val compareHash = compare!!.configs["102"].hashCode()
//			Log.d("ConfigDetectionTestx1", resultHash.toString())
//			Log.d("ConfigDetectionTestx1", compareHash.toString())
//			assert(resultHash == compareHash)

			val resultJSONString = gson.toJson(result!!.first)
			val compareJSONString = gson.toJson(compare!!.omrConfigs["102"])
			Log.d("ConfigDetectionTestx2", resultJSONString)
			Log.d("ConfigDetectionTestx2", compareJSONString)
			assert(resultJSONString == compareJSONString)
		}
	}
}
