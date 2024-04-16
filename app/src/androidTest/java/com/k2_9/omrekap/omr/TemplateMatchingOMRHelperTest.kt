package com.k2_9.omrekap.omr

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.k2_9.omrekap.R
import com.k2_9.omrekap.data.configs.omr.OMRCropper
import com.k2_9.omrekap.data.configs.omr.OMRCropperConfig
import com.k2_9.omrekap.data.configs.omr.OMRSection
import com.k2_9.omrekap.data.configs.omr.TemplateMatchingOMRDetectorConfig
import com.k2_9.omrekap.utils.omr.TemplateMatchingOMRHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils

@RunWith(AndroidJUnit4::class)
class TemplateMatchingOMRHelperTest {

	private var helper: TemplateMatchingOMRHelper
	init {
		OpenCVLoader.initLocal()

		val appContext = InstrumentationRegistry.getInstrumentation().targetContext

		// Load the image resource
		val image = Utils.loadResource(appContext, R.raw.example)

		val sectionPositions = mapOf(
			OMRSection.FIRST to Pair(780,373),
			OMRSection.SECOND to Pair(0, 0),
			OMRSection.THIRD to Pair(0, 0),
		)

		val cropperConfig = OMRCropperConfig(
			image,
			Pair(140, 220),
			sectionPositions
		)

		val cropper = OMRCropper(cropperConfig)

		// Load the template image resource
		val template = Utils.loadResource(appContext, R.raw.circle_template)

		val config = TemplateMatchingOMRDetectorConfig(
			cropper,
			template,
			0.7f
		)

		helper = TemplateMatchingOMRHelper(config)
	}

	@Test
	fun test_detect() {
		val result = helper.detect(OMRSection.FIRST)
		Log.d("TemplateMatchingOMRHelperTest", result.toString())
		assert(result == 172)
	}
}
