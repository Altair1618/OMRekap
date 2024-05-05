package com.k2_9.omrekap.views.activities

import android.content.Intent

class ResultFromGalleryActivity : ResultActivity() {
	private fun onBackHome() {
		val intent = Intent(this, HomeActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

		finish()
		startActivity(intent)
	}

	override fun handleBackNavigation() {
		onBackHome()
	}

	override fun getCameraIntent(): Intent {
		val intent = Intent(this, CameraActivity::class.java)
		intent.putExtra(CameraActivity.EXTRA_NAME_IS_FROM_RESULT, true)
		intent.putExtra(CameraActivity.EXTRA_NAME_IS_FROM_CAMERA_RESULT, false)
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
		return intent
	}
}
