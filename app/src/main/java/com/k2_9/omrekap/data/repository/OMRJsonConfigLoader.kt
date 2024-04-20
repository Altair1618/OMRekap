package com.k2_9.omrekap.data.repository

import com.google.gson.Gson
import com.k2_9.omrekap.data.models.OMRBaseConfiguration


object OMRJsonConfigLoader {
	private val gson = Gson()

	fun parsePlanoConfig(jsonString: String): OMRBaseConfiguration? {
		return gson.fromJson(jsonString, OMRBaseConfiguration::class.java)
	}

	fun toJson(omrBaseConfiguration: OMRBaseConfiguration): String {
		return gson.toJson(omrBaseConfiguration)
	}
}
