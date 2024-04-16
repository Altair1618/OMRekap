package com.k2_9.omrekap.omr

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.k2_9.omrekap.R
import com.k2_9.omrekap.data.configs.omr.OMRCropper
import com.k2_9.omrekap.data.configs.omr.OMRCropperConfig
import com.k2_9.omrekap.data.configs.omr.OMRSection
import com.k2_9.omrekap.data.models.ImageSaveData
import com.k2_9.omrekap.utils.SaveHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class OMRCropperTest {

	private var cropper: OMRCropper
	private var appContext: Context
	init {
		OpenCVLoader.initLocal()

		appContext = InstrumentationRegistry.getInstrumentation().targetContext

		// Load the image resource as a Bitmap
		val image = Utils.loadResource(appContext, R.raw.example)

		val sectionPositions = mapOf(
			OMRSection.FIRST to Pair(780,375),
			OMRSection.SECOND to Pair(0, 0),
			OMRSection.THIRD to Pair(0, 0),
		)

		val config = OMRCropperConfig(
			image,
			Pair(140, 225),
			sectionPositions
		)

		cropper = OMRCropper(config)
	}
	@Test
	fun test_crop() {
		val result = cropper.crop(OMRSection.FIRST)

		val bitmap = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888)
		Utils.matToBitmap(result, bitmap)

		val saveHelper = SaveHelper()

		saveHelper.saveImage(appContext, bitmap, "test", "test_crop.png")
		assert(result.width() == 140 && result.height() == 225)
	}
}
