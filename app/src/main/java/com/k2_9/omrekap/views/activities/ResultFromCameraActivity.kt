package com.k2_9.omrekap.views.activities

import android.content.Intent

class ResultFromCameraActivity : ResultActivity() {
	private fun onBackCamera() {
		val intent = Intent(this, CameraActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
		finish()
		startActivity(intent)
	}

	override fun handleBackNavigation() {
		onBackCamera()
	}

	override fun getCameraIntent(): Intent {
		val intent = Intent(this, CameraActivity::class.java)
		intent.putExtra(CameraActivity.EXTRA_NAME_IS_FROM_RESULT, true)
		intent.putExtra(CameraActivity.EXTRA_NAME_IS_FROM_CAMERA_RESULT, true)
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
		return intent
	}
}
