package com.k2_9.omrekap.utils.omr

import android.content.Context
import android.util.Log
import com.k2_9.omrekap.data.models.OMRBaseConfiguration
import com.k2_9.omrekap.data.models.OMRConfigurationParameter
import com.k2_9.omrekap.data.repository.OMRConfigRepository
import com.k2_9.omrekap.utils.AprilTagHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opencv.core.Mat

object OMRConfigurationDetector {
	private lateinit var loadedConfig: OMRBaseConfiguration

	/**
	 * Initialize and load the detection configuration data.
	 * Make sure to run this before detecting configurations
	 */
	suspend fun loadConfiguration(context: Context) {
		loadedConfig = OMRConfigRepository.loadConfigurations(context)
			?: throw Exception("Failed to load OMR Configuration!")
	}

	/**
	 * Detects the OMR configuration of an image to be processed.
	 * @param imageMat pre-processed image in gray or non color form
	 * @return Pair of OMR configuration and the image's tag corners that was used for configuration detector
	 */
	suspend fun detectConfiguration(imageMat: Mat):
		Pair<OMRConfigurationParameter, Mat>? {
		val configs = loadedConfig.omrConfigs

		var result: Pair<OMRConfigurationParameter, Mat>? = null
		withContext(Dispatchers.Default) {
			// get detected AprilTags
			val (ids, cornersList) = AprilTagHelper.getAprilTagId(imageMat)
			val nId = ids.size
			for (i in 0..<nId) {
				val id = ids[i]
				if (id in configs) {
					if (result == null) {
						result = configs[id]!! to cornersList[i]
					} else {
						Log.e(
							"OMRConfigurationDetector",
							"Multiple tags detected, unable to determine configuration"
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
