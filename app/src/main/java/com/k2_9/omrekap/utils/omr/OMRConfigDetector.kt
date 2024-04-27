package com.k2_9.omrekap.utils.omr

import android.content.Context
import android.util.Log
import com.k2_9.omrekap.data.models.OMRBaseConfiguration
import com.k2_9.omrekap.data.models.OMRConfigurationParameter
import com.k2_9.omrekap.data.repository.OMRConfigRepository
import com.k2_9.omrekap.utils.AprilTagHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.core.Mat

object OMRConfigDetector {
	private lateinit var loadedConfig: OMRBaseConfiguration
	private var job: Job? = null

	/**
	 * Initialize and load the detection configuration data.
	 * Make sure to run this before detecting configurations
	 */
	fun loadConfiguration(context: Context) {
		if (!this::loadedConfig.isInitialized) {
			job =
				CoroutineScope(Dispatchers.IO).launch {
					loadedConfig = OMRConfigRepository.loadConfigurations(context)
						?: throw Exception("Failed to load OMR Configuration!")
				}
		}
	}

	/**
	 * Detects the OMR configuration of an image to be processed.
	 * @param imageMat pre-processed image in gray or non color form
	 * @return Triple of OMR configuration, the ID of the detected AprilTag,
	 * and the image's tag corners that was used for configuration detector
	 */
	suspend fun detectConfiguration(imageMat: Mat): Triple<OMRConfigurationParameter, String, Mat>? {
		job?.join().also { job = null }
		val configs = loadedConfig.omrConfigs

		var result: Triple<OMRConfigurationParameter, String, Mat>? = null
		withContext(Dispatchers.Default) {
			// get detected AprilTags
			val (ids, cornersList) = AprilTagHelper.getAprilTagId(imageMat)
			val nId = ids.size
			for (i in 0..<nId) {
				val id = ids[i]
				if (id in configs) {
					if (result == null) {
						result = Triple(configs[id]!!, id, cornersList[i])
					} else {
						Log.e(
							"OMRConfigurationDetector",
							"Multiple tags detected, unable to determine configuration",
						)
						result = null
						break
					}
				}
			}
		}
		return result
	}
}
