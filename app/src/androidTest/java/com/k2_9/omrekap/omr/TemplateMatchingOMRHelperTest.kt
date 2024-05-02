package com.k2_9.omrekap.omr

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.k2_9.omrekap.R
import com.k2_9.omrekap.data.configs.omr.CircleTemplateLoader
import com.k2_9.omrekap.data.configs.omr.OMRSection
import com.k2_9.omrekap.utils.SaveHelper
import com.k2_9.omrekap.utils.omr.OMRConfigDetector
import com.k2_9.omrekap.utils.omr.TemplateMatchingOMRHelper
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

@RunWith(AndroidJUnit4::class)
class TemplateMatchingOMRHelperTest {
	private var helper: TemplateMatchingOMRHelper
	private val appContext: Context

	init {
		OpenCVLoader.initLocal()

		appContext = InstrumentationRegistry.getInstrumentation().targetContext

		// Load the image resource as a Bitmap
		val imageMat = Utils.loadResource(appContext, R.raw.test)
		val templateLoader = CircleTemplateLoader(appContext, R.raw.circle_template)

		// Convert if image is not grayscale
		val grayscaleImageMat =
			if (imageMat.channels() == 3) {
				val grayImageMat = Mat()
				Imgproc.cvtColor(imageMat, grayImageMat, Imgproc.COLOR_BGR2GRAY)
				grayImageMat
			} else {
				imageMat
			}

		runBlocking {
			// Get OMR Config by AprilTag
			OMRConfigDetector.loadConfiguration(appContext)
			val configResult = OMRConfigDetector.detectConfiguration(grayscaleImageMat)
			assert(configResult != null)

			val config = configResult!!.first

			config.contourOMRHelperConfig.omrCropper.config.setImage(grayscaleImageMat)
			config.templateMatchingOMRHelperConfig.omrCropper.config.setImage(grayscaleImageMat)
			config.templateMatchingOMRHelperConfig.setTemplate(templateLoader)

			helper = TemplateMatchingOMRHelper(config.templateMatchingOMRHelperConfig)
		}
	}

	@Test
	fun test_template_matching_omr() {
		val resultFirst = helper.detect(OMRSection.FIRST)
		val resultSecond = helper.detect(OMRSection.SECOND)
		val resultThird = helper.detect(OMRSection.THIRD)

		val imgFirst = helper.annotateImage(resultFirst)
		val imgSecond = helper.annotateImage(resultSecond)
		val imgThird = helper.annotateImage(resultThird)

		Log.d("TemplateMatchingOMRHelperTest", resultFirst.toString())
		Log.d("TemplateMatchingOMRHelperTest", resultSecond.toString())
		Log.d("TemplateMatchingOMRHelperTest", resultThird.toString())

		SaveHelper.saveImage(appContext, imgFirst, "test", "test_template_matching_omr_first")
		SaveHelper.saveImage(appContext, imgSecond, "test", "test_template_matching_omr_second")
		SaveHelper.saveImage(appContext, imgThird, "test", "test_template_matching_omr_third")

		assert(resultFirst == 172)
		assert(resultSecond == 24)
		assert(resultThird == 2)
	}
}
