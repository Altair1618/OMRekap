package com.k2_9.omrekap.views.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.k2_9.omrekap.R
import com.k2_9.omrekap.data.view_models.ImageDataViewModel
import com.k2_9.omrekap.utils.ImageSaveDataHolder
import com.k2_9.omrekap.views.activities.ExpandImageActivity
import com.k2_9.omrekap.views.activities.HomeActivity
import com.k2_9.omrekap.views.adapters.ResultAdapter
import java.io.FileOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 * Use the [ResultPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultPageFragment : Fragment() {
	companion object {
		const val BITMAP_FILE_NAME = "temp.png"
	}

	private var imageBitmap: Bitmap? = null

	private lateinit var recyclerView: RecyclerView
	private lateinit var resultAdapter: ResultAdapter
	private lateinit var documentImageView: ImageView
	private lateinit var timestampTextView: TextView
	private lateinit var failureTextView: TextView

	private lateinit var viewModel: ImageDataViewModel

	private fun onHomeButtonClick() {
		val intent = Intent(context, HomeActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

		activity?.finish()
		startActivity(intent)
	}

	private fun timestampToString(timestamp: Instant): String {
		val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
		return formatter.format(timestamp.atZone(ZoneId.systemDefault()))
	}

	private fun showFailureText() {
		failureTextView.visibility = View.VISIBLE
		recyclerView.visibility = View.GONE
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
	}

	private fun hideFailureText() {
		failureTextView.visibility = View.GONE
		recyclerView.visibility = View.VISIBLE
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		viewModel = ViewModelProvider(requireActivity())[ImageDataViewModel::class.java]

		imageBitmap = ImageSaveDataHolder.get().annotatedImage

		viewModel.data.observe(this) {
			// TODO: fix this block that making fragment cant be replaced
			val dataList = it.data.toList()

			if (dataList.isEmpty()) {
				showFailureText()
			} else {
				hideFailureText()

				val result =
					dataList.map { (key, value) ->
						key to (value?.toString() ?: "undetected")
					}

				resultAdapter.submitList(result)
			}

			val anotatedImage = it.annotatedImage

			// change expand image
			imageBitmap = anotatedImage

			// change result image
			documentImageView.setImageBitmap(anotatedImage)

			// change timestamp
			timestampTextView.text = timestampToString(it.timestamp)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?,
	): View? {
		val view = inflater.inflate(R.layout.fragment_result_page, container, false)

		// Initialize your data (list of key-value pairs)
		val resultData =
			listOf(
				Pair("Candidate", "Vote count"),
			)

		// remove shadow
		val resultCardView: CardView = view.findViewById(R.id.result_card_view)
		val homeCardView: CardView = view.findViewById(R.id.home_card_view)

		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
			resultCardView.elevation = 0f
			homeCardView.elevation = 0f
		}

		// Set up RecyclerView
		recyclerView = view.findViewById(R.id.result_recycler_view)
		recyclerView.layoutManager = LinearLayoutManager(requireContext())
		resultAdapter = ResultAdapter()
		recyclerView.adapter = resultAdapter

		resultAdapter.submitList(resultData)

		// link image URI with view
		documentImageView = view.findViewById(R.id.document_image)
		documentImageView.setImageBitmap(imageBitmap)

		// timestamp text
		timestampTextView = view.findViewById(R.id.result_timestamp)
		timestampTextView.text = timestampToString(ImageSaveDataHolder.get().timestamp)

		// failure text
		failureTextView = view.findViewById(R.id.failure_text)
		hideFailureText()

		// remove progress bar
		val progressLoader: ProgressBar = view.findViewById(R.id.progress_loader)
		progressLoader.visibility = View.GONE

		// configure expand action
		val expandButton: ImageButton = view.findViewById(R.id.expand_button)
		expandButton.setOnClickListener {
			// Pass the image resource ID to ExpandImageActivity
			val imageResource = imageBitmap ?: R.drawable.ic_image
			val intent = Intent(requireContext(), ExpandImageActivity::class.java)

			// Choose the appropriate constant based on the type of resource
			when (imageResource) {
				is Int -> {
					intent.putExtra(ExpandImageActivity.EXTRA_NAME_DRAWABLE_RESOURCE, imageResource)
				}
				is Bitmap -> {
					val stream: FileOutputStream =
						requireActivity().openFileOutput(BITMAP_FILE_NAME, Context.MODE_PRIVATE)
					imageResource.compress(Bitmap.CompressFormat.PNG, 100, stream)
					stream.close()

					intent.putExtra(ExpandImageActivity.EXTRA_NAME_IMAGE_RESOURCE, BITMAP_FILE_NAME)
				}
				else -> {
					throw IllegalArgumentException("Unsupported resource type")
				}
			}

			startActivity(intent)
		}

		// set home button listener
		val homeButton: ImageButton = view.findViewById(R.id.home_button)
		homeButton.setOnClickListener {
			onHomeButtonClick()
		}

		return view
	}
}
