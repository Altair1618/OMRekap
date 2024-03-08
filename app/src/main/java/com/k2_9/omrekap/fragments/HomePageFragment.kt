package com.k2_9.omrekap.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.k2_9.omrekap.R

class HomePageFragment : Fragment() {
	private fun handleBackNavigation() {
		activity?.finishAffinity()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		requireActivity().onBackPressedDispatcher.addCallback(
			this,
			object : OnBackPressedCallback(true) {
				override fun handleOnBackPressed() {
					handleBackNavigation()
				}
			},
		)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?,
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_home_page, container, false)
	}

	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?,
	) {
		super.onViewCreated(view, savedInstanceState)
	}
}
