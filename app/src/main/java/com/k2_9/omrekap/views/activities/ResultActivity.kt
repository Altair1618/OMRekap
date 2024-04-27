package com.k2_9.omrekap.views.activities

import android.Manifest
import android.content.Intent
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
import com.k2_9.omrekap.R
import com.k2_9.omrekap.data.configs.omr.CircleTemplateLoader
import com.k2_9.omrekap.data.models.ImageSaveData
import com.k2_9.omrekap.data.view_models.ImageDataViewModel
import com.k2_9.omrekap.utils.ImageSaveDataHolder
import com.k2_9.omrekap.utils.PermissionHelper
import com.k2_9.omrekap.utils.SaveHelper
import com.k2_9.omrekap.utils.omr.OMRConfigDetector
import com.k2_9.omrekap.views.fragments.ResultPageFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader

abstract class ResultActivity : MainActivity() {
	companion object {
		const val EXTRA_NAME_IS_RESET = "IS_RESET"
	}

	private val viewModel: ImageDataViewModel by viewModels()
	private var saveFileJob: Job? = null
	private var startSaveJob: Boolean = false
	private val omrHelperObserver =
		Observer<ImageSaveData?> { data ->
			if (data == null) {
				Log.e("MainActivity", "Data is null")
				return@Observer
			}

			saveFile()
		}

	private var isReset: Boolean = false // reset ViewModel for new OMR process
	private var isCreated = false

	private fun updateStates(intent: Intent) {
		isReset = intent.getBooleanExtra(EXTRA_NAME_IS_RESET, false)

		if (isReset) {
			viewModel.resetState()

			if (isCreated) {
				setFragment(intent)
			}
		}

		if (viewModel.data.value == null) {
			viewModel.data.observe(this, omrHelperObserver)
			val templateLoader = CircleTemplateLoader(applicationContext, R.raw.circle_template)
			viewModel.processImage(ImageSaveDataHolder.get(), templateLoader)
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

		val arguments =
			Bundle().apply {
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

		OMRConfigDetector.loadConfiguration(this)
		OpenCVLoader.initLocal()

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
			PermissionHelper.requirePermission(
				this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				false,
			) {}
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
