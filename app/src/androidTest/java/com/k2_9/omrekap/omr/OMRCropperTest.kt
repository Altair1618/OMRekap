package com.k2_9.omrekap.omr

import android.content.Context
import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.k2_9.omrekap.R
import com.k2_9.omrekap.data.configs.omr.CircleTemplateLoader
import com.k2_9.omrekap.data.configs.omr.OMRCropper
import com.k2_9.omrekap.data.configs.omr.OMRSection
import com.k2_9.omrekap.utils.SaveHelper
import com.k2_9.omrekap.utils.omr.OMRConfigDetector
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

@RunWith(AndroidJUnit4::class)
class OMRCropperTest {
	private var cropper: OMRCropper
	private var appContext: Context

	init {
		OpenCVLoader.initLocal()

		appContext = InstrumentationRegistry.getInstrumentation().targetContext

		// Load the image resource as a Bitmap
		val imageMat = Utils.loadResource(appContext, R.raw.example)
		val templateLoader = CircleTemplateLoader(appContext, R.raw.circle_template)

		// Convert if image is not grayscale
		val grayscaleImageMat = if (imageMat.channels() == 3) {
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

			cropper = OMRCropper(config.contourOMRHelperConfig.omrCropper.config)
		}
	}

	@Test
	fun test_crop() {
		val resultFirst = cropper.crop(OMRSection.FIRST)
		val resultSecond = cropper.crop(OMRSection.SECOND)
		val resultThird = cropper.crop(OMRSection.THIRD)

		val bitmapFirst = Bitmap.createBitmap(resultFirst.cols(), resultFirst.rows(), Bitmap.Config.ARGB_8888)
		val bitmapSecond = Bitmap.createBitmap(resultSecond.cols(), resultSecond.rows(), Bitmap.Config.ARGB_8888)
		val bitmapThird = Bitmap.createBitmap(resultThird.cols(), resultThird.rows(), Bitmap.Config.ARGB_8888)

		Utils.matToBitmap(resultFirst, bitmapFirst)
		Utils.matToBitmap(resultSecond, bitmapSecond)
		Utils.matToBitmap(resultThird, bitmapThird)

		SaveHelper.saveImage(appContext, bitmapFirst, "test", "test_crop_first.png")
		SaveHelper.saveImage(appContext, bitmapSecond, "test", "test_crop_second.png")
		SaveHelper.saveImage(appContext, bitmapThird, "test", "test_crop_third.png")

		assert(resultFirst.width() == 80 && resultFirst.height() == 124)
		assert(resultSecond.width() == 80 && resultSecond.height() == 124)
		assert(resultThird.width() == 80 && resultThird.height() == 124)
	}
}
