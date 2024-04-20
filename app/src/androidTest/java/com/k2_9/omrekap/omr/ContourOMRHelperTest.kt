package com.k2_9.omrekap.omr

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.k2_9.omrekap.R
import com.k2_9.omrekap.data.configs.omr.ContourOMRHelperConfig
import com.k2_9.omrekap.data.configs.omr.OMRCropper
import com.k2_9.omrekap.data.configs.omr.OMRCropperConfig
import com.k2_9.omrekap.data.configs.omr.OMRSection
import com.k2_9.omrekap.utils.omr.ContourOMRHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils

@RunWith(AndroidJUnit4::class)
class ContourOMRHelperTest {
	private var helper: ContourOMRHelper

	init {
		OpenCVLoader.initLocal()

		val appContext = InstrumentationRegistry.getInstrumentation().targetContext

		// Load the image resource as a Bitmap
		val image = Utils.loadResource(appContext, R.raw.example)

		val sectionPositions =
			mapOf(
				OMRSection.FIRST to Pair(780, 373),
				OMRSection.SECOND to Pair(0, 0),
				OMRSection.THIRD to Pair(0, 0),
			)

		val cropperConfig =
			OMRCropperConfig(
				image,
				Pair(140, 220),
				sectionPositions,
			)

		val cropper = OMRCropper(cropperConfig)

		val config =
			ContourOMRHelperConfig(
				cropper,
				12,
				30,
				0.5f,
				1.5f,
				0.9f,
				230,
			)

		helper = ContourOMRHelper(config)
	}

	@Test
	fun test_detect() {
		val result = helper.detect(OMRSection.FIRST)
		Log.d("ContourOMRHelperTest", result.toString())
		assert(result == 172)
	}
}
