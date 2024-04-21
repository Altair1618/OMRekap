package com.k2_9.omrekap.views.activities

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.k2_9.omrekap.data.models.ImageSaveData
import com.k2_9.omrekap.data.view_models.ImageDataViewModel
import com.k2_9.omrekap.utils.ImageSaveDataHolder
import com.k2_9.omrekap.utils.PermissionHelper
import com.k2_9.omrekap.utils.SaveHelper
import com.k2_9.omrekap.views.fragments.ResultPageFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader

abstract class ResultActivity : MainActivity() {
	companion object {
		const val EXTRA_NAME_IMAGE_URI_STRING = "IMAGE_URI_STRING"
		const val EXTRA_NAME_IS_RESET = "IS_RESET"
	}

	private val viewModel: ImageDataViewModel by viewModels()
	private var saveFileJob: Job? = null
	private var startSaveJob: Boolean = false
	private val omrHelperObserver =
		Observer<ImageSaveData> { newValue ->
			saveFile()

//			TODO: save file when data is not empty after implemented
//			if (newValue.data.isNotEmpty()) {
//				saveFile()
//			}
		}

	private lateinit var imageUriString: String
	private lateinit var imageBitmap: Bitmap
	private var isReset: Boolean = false // reset ViewModel for new OMR process
	private var isCreated = false

	private fun updateStates(intent: Intent) {
		isReset = intent.getBooleanExtra(EXTRA_NAME_IS_RESET, false)
		Log.d("RESET GA YA", isReset.toString())
		val uriString =
			intent.getStringExtra(EXTRA_NAME_IMAGE_URI_STRING)
				?: throw IllegalArgumentException("Image URI string is null")

		imageUriString = uriString

		if (isReset) {
			// TODO: reset view model (perlu diskusi dulu tentang stop proses kalau ganti page)
			Log.d("CREATED GA YA", isCreated.toString())
			if (isCreated) {
				setFragment(intent)
			}
		}

		imageBitmap = SaveHelper.uriToBitmap(applicationContext, Uri.parse(imageUriString))

		if (viewModel.data.value == null) {
			viewModel.processImage(ImageSaveDataHolder.get())
			viewModel.data.observe(this, omrHelperObserver)
		}
	}

	private fun saveFile() {
		saveFileJob =
			lifecycleScope.launch(Dispatchers.IO) {
				startSaveJob = true
				SaveHelper.save(applicationContext, viewModel.data.value!!)
				startSaveJob = false

				withContext(Dispatchers.Main) {
					Toast.makeText(
						applicationContext,
						"File saved in Documents/OMRekap",
						Toast.LENGTH_SHORT,
					).show()
				}
			}
	}

	override fun getFragment(intent: Intent): Fragment {
		val fragment = ResultPageFragment()

		val uriString =
			intent.getStringExtra(EXTRA_NAME_IMAGE_URI_STRING)
				?: throw IllegalArgumentException("Image URI string is null")

		val arguments =
			Bundle().apply {
				putString(ResultPageFragment.ARG_NAME_IMAGE_URI_STRING, uriString)
			}

		// Set the arguments for the fragment
		fragment.arguments = arguments
		return fragment
	}

	override fun getGalleryPreviewIntent(imageUri: Uri): Intent {
		val intent = Intent(this, PreviewActivity::class.java)

		intent.putExtra(PreviewActivity.EXTRA_NAME_IS_RESET, true)
		intent.putExtra(PreviewActivity.EXTRA_NAME_IS_FROM_CAMERA, false)
		intent.putExtra(PreviewActivity.EXTRA_NAME_IMAGE_URI_STRING, imageUri.toString())

		return intent
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)

		if (intent != null) {
			updateStates(intent)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		OpenCVLoader.initLocal()

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
			PermissionHelper.requirePermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, false) {}
		}

		startSaveJob = savedInstanceState?.getBoolean("startSaveJob") ?: false
		if (startSaveJob) {
			if (viewModel.data.value != null && viewModel.data.value?.annotatedImage != null &&
				viewModel.data.value?.data != null
			) {
				saveFile()
			} else {
				Log.e("MainActivity", "startSaveJob is true but data is not processed")
			}
		}

		updateStates(intent)

		isCreated = true
	}

	override fun onSaveInstanceState(
		outState: Bundle,
		outPersistentState: PersistableBundle,
	) {
		super.onSaveInstanceState(outState, outPersistentState)
		outState.putBoolean("startSaveJob", startSaveJob)
	}

	override fun onDestroy() {
		super.onDestroy()
		saveFileJob?.cancel()
	}
}
