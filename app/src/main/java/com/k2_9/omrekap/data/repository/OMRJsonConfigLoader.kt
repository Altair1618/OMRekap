package com.k2_9.omrekap.data.repository

import com.google.gson.Gson
import com.k2_9.omrekap.data.models.OMRBaseConfiguration

/**
 * JSON configuration loader for OMR configurations
 */
object OMRJsonConfigLoader {
	private val gson = Gson()

	/**
	 * Parse JSON string to OMRBaseConfiguration object
	 * @param jsonString JSON string
	 * @return OMRBaseConfiguration object
	 */
	fun parseJson(jsonString: String): OMRBaseConfiguration? {
		return gson.fromJson(jsonString, OMRBaseConfiguration::class.java)
	}

	/**
	 * Convert OMRBaseConfiguration object to JSON string
	 * @param omrBaseConfiguration OMRBaseConfiguration object
	 * @return JSON string
	 */
	fun toJson(omrBaseConfiguration: OMRBaseConfiguration): String {
		return gson.toJson(omrBaseConfiguration)
	}
}
