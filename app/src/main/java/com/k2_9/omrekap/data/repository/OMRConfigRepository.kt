package com.k2_9.omrekap.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.k2_9.omrekap.data.models.OMRBaseConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

object OMRConfigRepository {
	suspend fun loadConfigurations(context: Context): OMRBaseConfiguration? {
		val jsonString = withContext(Dispatchers.IO) {
			readConfigString(context)
		}
		return if (jsonString == null) {
			Toast.makeText(context, "Error! Unable to read configuration", Toast.LENGTH_SHORT)
				.show()
			null
		} else {
			OMRJsonConfigLoader.parsePlanoConfig(jsonString)
		}
	}

	fun printConfigurationJson(omrBaseConfiguration: OMRBaseConfiguration): String {
		val jsonString = OMRJsonConfigLoader.toJson(omrBaseConfiguration)
		Log.d("JSONConfigRepo", jsonString)
		return jsonString
	}

	private fun readConfigString(context: Context): String? {
		val inputStream = context.assets.open("omr_config.json")
		return try {
			val buffer = ByteArray(inputStream.available())
			inputStream.read(buffer)
			Log.d("OMRConfigLoader", String(buffer))

			String(buffer)
		} catch (e: IOException) {
			// Handle config read error
			null
		} finally {
			inputStream.close()
		}
	}
}
