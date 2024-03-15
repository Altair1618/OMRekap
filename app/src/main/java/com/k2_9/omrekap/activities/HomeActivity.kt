package com.k2_9.omrekap.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.k2_9.omrekap.fragments.HomePageFragment

class HomeActivity: MainActivity() {

	override fun getGalleryPreviewIntent(imageUri:Uri): Intent {
		val intent = Intent(this, PreviewActivity::class.java)

		intent.putExtra(PreviewActivity.EXTRA_NAME_IS_RESET, true)
		intent.putExtra(PreviewActivity.EXTRA_NAME_IS_FROM_CAMERA, false)
		intent.putExtra(PreviewActivity.EXTRA_NAME_IMAGE_URI_STRING, imageUri.toString())

		return intent
	}
	override fun getCameraIntent(): Intent {
		val intent = Intent(this, CameraActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
		return intent
	}

	override fun getFragment(): Fragment {
		return HomePageFragment()
	}

	override fun handleBackNavigation() {
		finishAffinity()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}
}
