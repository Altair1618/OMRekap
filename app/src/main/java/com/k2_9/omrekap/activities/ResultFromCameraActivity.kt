package com.k2_9.omrekap.activities

import android.content.Intent
import android.util.Log

class ResultFromCameraActivity : ResultActivity() {
	private fun onBackCamera() {
		val intent = Intent(this, CameraActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
		Log.d("wtf is this", "onBackCamera: ")
		finish()
		startActivity(intent)
	}

	override fun handleBackNavigation() {
		onBackCamera()
	}

	override fun getCameraIntent(): Intent {
		val uriString =
			intent.getStringExtra(EXTRA_NAME_IMAGE_URI_STRING)
				?: throw IllegalArgumentException("Image URI string is null")

		val intent = Intent(this, CameraActivity::class.java)
		intent.putExtra(CameraActivity.EXTRA_NAME_IMAGE_URI_STRING, uriString)
		intent.putExtra(CameraActivity.EXTRA_NAME_IS_FROM_CAMERA_RESULT, true)
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
		return intent
	}
}
