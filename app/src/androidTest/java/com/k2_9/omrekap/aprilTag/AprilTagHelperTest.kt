package com.k2_9.omrekap.aprilTag

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.k2_9.omrekap.R
import com.k2_9.omrekap.utils.AprilTagHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils

@RunWith(AndroidJUnit4::class)
class AprilTagHelperTest {
	private val helper: AprilTagHelper = AprilTagHelper

	@Test
	fun testAprilTagDetection() {
		OpenCVLoader.initLocal()

		val appContext = InstrumentationRegistry.getInstrumentation().targetContext

		// Load the image resource as a Bitmap
		val image = Utils.loadResource(appContext, R.raw.example)

		// Call the method to detect AprilTag
		val result = helper.getAprilTagId(image)
		Log.d("ContourOMRHelperTest", result.toString())
	}
}
