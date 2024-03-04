package com.k2_9.omrekap.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.k2_9.omrekap.R
import com.k2_9.omrekap.activities.ExpandImageActivity
import com.k2_9.omrekap.adapters.ResultAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [ResultPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultPageFragment : Fragment() {
	interface OnButtonClickListener {
		fun onHomeButtonClick()
	}

	private lateinit var recyclerView: RecyclerView
	private lateinit var resultAdapter: ResultAdapter

	private var buttonClickListener: OnButtonClickListener? = null

	override fun onAttach(context: Context) {
		super.onAttach(context)

		if (context is OnButtonClickListener) {
			buttonClickListener = context
		} else {
			throw ClassCastException("$context must implement OnButtonClickListener")
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
				Pair("Prabowo Subianto Djojohadikusumo", "270,20 juta"),
				Pair("Key2", "Value2"),
				Pair("Key3", "Value3"),
				Pair("Key4", "Value4"),
				Pair("Key5", "Value5"),
				Pair("Key6", "Value6"),
				// ... add more key-value pairs as needed
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
		resultAdapter = ResultAdapter(resultData)
		recyclerView.adapter = resultAdapter

		// set default image
		val documentImageView: ImageView = view.findViewById(R.id.document_image)
		val defaultDrawableId = R.drawable.ic_image

		documentImageView.tag = defaultDrawableId
		documentImageView.setImageResource(defaultDrawableId)

		// configure expand action
		val expandButton: ImageButton = view.findViewById(R.id.expand_button)
		expandButton.setOnClickListener {
			// Pass the image resource ID to ExpandImageActivity
			val imageResource = documentImageView.tag ?: R.drawable.ic_image
			val intent = Intent(requireContext(), ExpandImageActivity::class.java)

			// Choose the appropriate constant based on the type of resource
			when (imageResource) {
				is Int -> {
					intent.putExtra(ExpandImageActivity.EXTRA_NAME_DRAWABLE_RESOURCE, imageResource)
				}
				is Uri -> {
					intent.putExtra(ExpandImageActivity.EXTRA_NAME_IMAGE_RESOURCE, imageResource.toString())
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
			buttonClickListener?.onHomeButtonClick()
		}

		return view
	}
}
