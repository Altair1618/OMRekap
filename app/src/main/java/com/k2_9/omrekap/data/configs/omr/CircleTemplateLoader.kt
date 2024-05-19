package com.k2_9.omrekap.data.configs.omr

import android.content.Context
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.io.InputStream

/**
 * Load the circle template image
 * @param appContext application context
 * @param resId resource id of the circle template image
 */
class CircleTemplateLoader(private val appContext: Context, private val resId: Int) {
	/**
	 * Load the template image
	 * @return template image
	 */
	fun loadTemplateImage(): Mat {
		val inputStream: InputStream = appContext.resources.openRawResource(resId)
		val byteArray = inputStream.readBytes()
		val imgBuffer = MatOfByte(*byteArray)
		return Imgcodecs.imdecode(imgBuffer, Imgcodecs.IMREAD_GRAYSCALE)
	}
}
