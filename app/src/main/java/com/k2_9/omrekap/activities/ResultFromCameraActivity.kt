package com.k2_9.omrekap.activities

import android.content.Intent
import android.net.Uri

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

	override fun getGalleryPreviewIntent(imageUri: Uri): Intent {
		val intent = Intent(this, PreviewActivity::class.java)

		intent.putExtra(PreviewActivity.EXTRA_NAME_IS_RESET, true)
		intent.putExtra(PreviewActivity.EXTRA_NAME_IS_FROM_CAMERA, true)
		intent.putExtra(PreviewActivity.EXTRA_NAME_IMAGE_URI_STRING, imageUri.toString())

		return intent
	}

	override fun getCameraIntent(): Intent {
		val intent = Intent(this, CameraActivity::class.java)
		intent.putExtra(CameraActivity.EXTRA_NAME_IMAGE_URI_STRING, getImageUriString())
		intent.putExtra(CameraActivity.EXTRA_NAME_IS_FROM_CAMERA_RESULT, true)
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
		return intent
	}
}
