package com.k2_9.omrekap.preprocess

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.k2_9.omrekap.R
import com.k2_9.omrekap.utils.CropHelper
import com.k2_9.omrekap.utils.SaveHelper
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc


@RunWith(JUnit4::class)
class CropHelperTest {
	private val image: Mat
	private val patternImage: Mat
	private val imageBitmap: Bitmap
	private val patternBitmap: Bitmap
	private val appContext: Context
	init {
		OpenCVLoader.initLocal()




		appContext = InstrumentationRegistry.getInstrumentation().targetContext
		image = Utils.loadResource(appContext, R.raw.example, CvType.CV_8UC1)
		patternImage = Utils.loadResource(appContext, R.raw.corner_pattern, CvType.CV_8UC4)

		patternBitmap = Bitmap.createBitmap(patternImage.width(), patternImage.height(), Bitmap.Config.ARGB_8888)
		imageBitmap = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.ARGB_8888)
		Utils.matToBitmap(image, imageBitmap)
		Utils.matToBitmap(patternImage, patternBitmap)
	}

	@Test
	fun test_preprocess_and_crop() {
		val cornerPoints = CropHelper.detectCorner(imageBitmap, patternImage)
		val result = CropHelper.fourPointTransform(
			imageBitmap,
			cornerPoints
		)
		Log.d("test_crop", cornerPoints.toString())
		Log.d("test_crop", "size ${patternImage.width()} x ${patternImage.height()}")
		SaveHelper.saveImage(appContext, patternBitmap, "test", "img_pattern_init")
		SaveHelper.saveImage(appContext, imageBitmap, "test", "img_example_init")
		SaveHelper.saveImage(appContext, result, "test", "test_preprocess_corner")
	}

	@After
	fun clear() {

	}
}
