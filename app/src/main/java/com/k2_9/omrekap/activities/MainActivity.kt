package com.k2_9.omrekap.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.k2_9.omrekap.R
import com.k2_9.omrekap.fragments.HomePageFragment
import com.k2_9.omrekap.fragments.ResultPageFragment
import com.k2_9.omrekap.models.ImageSaveData
import com.k2_9.omrekap.utils.SaveHelper
import com.k2_9.omrekap.view_models.ImageDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
	companion object {
		const val EXTRA_NAME_IS_RESULT = "IS_RESULT"
		const val EXTRA_NAME_IS_FROM_CAMERA = "IS_FROM_CAMERA"
		const val EXTRA_NAME_IMAGE_URI_STRING = "IMAGE_URI_STRING"
		const val EXTRA_NAME_IS_RESET = "IS_RESET"
	}

	private val viewModel: ImageDataViewModel by viewModels()
	private var saveFileJob: Job? = null
	private var saveFileJobCompleted: Boolean = false
	private val omrHelperObserver =
		Observer<ImageSaveData> { newValue ->
			if (newValue.isProcessed()) {
				saveFile()
			}
		}

	private var isResult: Boolean = false
	private var isFromCamera: Boolean = false // must be false if isResult is false
	private var imageUriString: String? = null // can't be null if isResult is true
	private var isReset: Boolean = false // reset ViewModel for new OMR process

	private var isCreated = false

	private fun onGalleryButtonClick() {
		val intent = Intent(this, MainActivity::class.java)

		intent.putExtra(EXTRA_NAME_IS_RESULT, true)
		intent.putExtra(EXTRA_NAME_IS_RESET, true)
		intent.putExtra(EXTRA_NAME_IS_FROM_CAMERA, false)
		// TODO: pass image URI from gallery
		intent.putExtra(EXTRA_NAME_IMAGE_URI_STRING, "")
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

		startActivity(intent)
	}

	private fun onCameraButtonClick() {
		val intent = Intent(this, CameraActivity::class.java)

		if (isResult) {
			intent.putExtra(CameraActivity.EXTRA_NAME_IMAGE_URI_STRING, imageUriString)
			intent.putExtra(CameraActivity.EXTRA_NAME_IS_FROM_CAMERA_RESULT, isFromCamera)
		}

		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)

		startActivity(intent)
	}

	private fun saveFile() {
		saveFileJob =
			lifecycleScope.launch(Dispatchers.IO) {
				SaveHelper().save(applicationContext, viewModel.data.value!!)
				saveFileJobCompleted = true

				withContext(Dispatchers.Main) {
					Toast.makeText(
						applicationContext,
						"File saved in Documents/OMRekap",
						Toast.LENGTH_SHORT,
					).show()
				}
			}
	}

	private fun setFragment() {
		// set fragment view
		val fragmentManager = supportFragmentManager
		val fragmentTransaction = fragmentManager.beginTransaction()
		var fragment: Fragment? = null
		if (isResult) {
			// Create an instance of your fragment
			fragment = ResultPageFragment()

			val arguments =
				Bundle().apply {
					putBoolean(ResultPageFragment.ARG_NAME_IS_FROM_CAMERA, isFromCamera)
					// TODO: PASS IMAGE URI STRING
				}

			// Set the arguments for the fragment
			fragment.arguments = arguments
		} else {
			fragment = HomePageFragment()
		}

		fragmentTransaction.replace(R.id.fragment_container_view, fragment)
		fragmentTransaction.commit()
	}

	private fun updateStates(intent: Intent) {
		isResult = intent.getBooleanExtra(EXTRA_NAME_IS_RESULT, false)

		if (isResult) {
			isFromCamera = intent.getBooleanExtra(EXTRA_NAME_IS_FROM_CAMERA, false)
			isReset = intent.getBooleanExtra(EXTRA_NAME_IS_RESET, false)
			imageUriString = intent.getStringExtra(EXTRA_NAME_IMAGE_URI_STRING)

			if (imageUriString == null) {
				throw IllegalArgumentException("Image URI string is null")
			}

			if (isReset) {
				// TODO: reset view model

				if (isCreated) {
					setFragment()
				}
			}

			if (viewModel.data.value == null || !viewModel.data.value!!.isProcessed()) {
				viewModel.processImage(Uri.parse(imageUriString))
				viewModel.data.observe(this, omrHelperObserver)
			}

			if (!isFromCamera && !saveFileJobCompleted && viewModel.data.value!!.isProcessed()) {
				saveFile()
			}
		}
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)

		if (intent != null) {
			if (isCreated && (isResult.xor(intent.getBooleanExtra(EXTRA_NAME_IS_RESULT, false)))) {
				// wrong activity reused, restarting the new intent and finishing this activity
				val newIntent = Intent(this, MainActivity::class.java)

				val extras = Bundle(intent.extras)
				newIntent.putExtras(extras)

				if (isResult) {
					// if want to go to home page but found result page on stack
					// remove this activity from stack and continue to find reusable homepage
					newIntent.addFlags(intent.flags)
					finish()
				}

				// if want to reuse result page but found home page, leave this home page activity
				// just create new result page

				startActivity(newIntent)
			} else {
				updateStates(intent)
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		saveFileJobCompleted = savedInstanceState?.getBoolean("saveFileJobCompleted") ?: false

		updateStates(intent)

		assert(!isFromCamera || isResult)

		// fix shadow
		val galleryCardView: CardView = findViewById(R.id.gallery_card_view)
		val cameraCardView: CardView = findViewById(R.id.camera_card_view)

		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
			galleryCardView.elevation = 0f
			cameraCardView.elevation = 0f
		}

		setFragment()

		// add button listeners
		val galleryButton: ImageButton = findViewById(R.id.gallery_button)
		val cameraButton: ImageButton = findViewById(R.id.camera_button)

		galleryButton.setOnClickListener {
			onGalleryButtonClick()
		}

		cameraButton.setOnClickListener {
			onCameraButtonClick()
		}

		isCreated = true
	}

	override fun onSaveInstanceState(
		outState: Bundle,
		outPersistentState: PersistableBundle,
	) {
		super.onSaveInstanceState(outState, outPersistentState)
		outState.putBoolean("saveFileJobCompleted", saveFileJobCompleted)
	}

	override fun onDestroy() {
		super.onDestroy()
		saveFileJob?.cancel()
	}
}
