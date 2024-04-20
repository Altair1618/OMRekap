package com.k2_9.omrekap.preprocess

import android.content.Context
import android.graphics.Bitmap
import androidx.test.platform.app.InstrumentationRegistry
import com.k2_9.omrekap.R
import com.k2_9.omrekap.data.models.ImageSaveData
import com.k2_9.omrekap.utils.CropHelper
import com.k2_9.omrekap.utils.PreprocessHelper
import com.k2_9.omrekap.utils.SaveHelper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat

@RunWith(JUnit4::class)
class CropHelperTest {
	private val image: Mat
	private val patternImage: Mat
	private val imageBitmap: Bitmap
	private val patternBitmap: Bitmap
	private val appContext: Context

	private var imageSaveData: ImageSaveData

	init {
		OpenCVLoader.initLocal()

		appContext = InstrumentationRegistry.getInstrumentation().targetContext
		image = Utils.loadResource(appContext, R.raw.example, CvType.CV_8UC1)
		patternImage = Utils.loadResource(appContext, R.raw.corner_pattern, CvType.CV_8UC4)

		patternBitmap = Bitmap.createBitmap(patternImage.width(), patternImage.height(), Bitmap.Config.ARGB_8888)
		imageBitmap = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.ARGB_8888)
		Utils.matToBitmap(image, imageBitmap)
		Utils.matToBitmap(patternImage, patternBitmap)

		CropHelper.loadPattern(patternBitmap)

		imageSaveData = ImageSaveData(imageBitmap, imageBitmap, mutableMapOf<String, Int>())
	}

	@Before
	fun beforeEachTest() {
		imageSaveData = ImageSaveData(imageBitmap, imageBitmap, mutableMapOf<String, Int>())
	}

	@Test
	fun test_preprocess_and_crop() {
		CropHelper.loadPattern(patternBitmap)
		imageSaveData = PreprocessHelper.preprocessImage(imageSaveData)

		SaveHelper.saveImage(appContext, imageSaveData.rawImage, "test", "test_preprocess_raw")
		SaveHelper.saveImage(appContext, imageSaveData.annotatedImage, "test", "test_preprocess_annotated")
	}

	@After
	fun clear() {
	}
}
