package com.k2_9.omrekap.utils

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * Helper class for handling permissions
 */
object PermissionHelper {
	/**
	 * Request permission from the user
	 * @param activity activity context
	 * @param permission permission to be requested
	 * @param verbose show toast message if permission is denied
	 * @param operation operation to be executed if permission is granted
	 */
	fun requirePermission(
		activity: AppCompatActivity,
		permission: String,
		verbose: Boolean = true,
		operation: () -> Unit,
	) {
		if (ContextCompat.checkSelfPermission(
				activity,
				permission,
			) == PackageManager.PERMISSION_GRANTED
		) {
			operation()
		} else {
			val requestPermissionLauncher =
				activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
						isGranted: Boolean ->
					if (isGranted) {
						operation()
					} else {
						if (verbose) {
							Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
						}
					}
				}
			requestPermissionLauncher.launch(permission)
		}
	}
}
