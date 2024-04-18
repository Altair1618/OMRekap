package com.k2_9.omrekap.data.configs.omr

import android.content.Context
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.io.InputStream

class CircleTemplateLoader(private val appContext: Context, private val resId: Int) {
	fun loadTemplateImage(): Mat {
		val inputStream: InputStream = appContext.resources.openRawResource(resId)
		val byteArray = inputStream.readBytes()
		val imgBuffer = MatOfByte(*byteArray)
		return Imgcodecs.imdecode(imgBuffer, Imgcodecs.IMREAD_GRAYSCALE)
	}
}
