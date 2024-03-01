package com.k2_9.omrekap

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class HomePageFragment: Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?,
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_home_page, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// Gradient for title shadow
		val textView:TextView = view.findViewById(R.id.title_shadow)
		val startColor = ContextCompat.getColor(requireContext(), R.color.white_opacity_16)
		val endColor = ContextCompat.getColor(requireContext(), R.color.white_opacity_0)
		val shader = LinearGradient(
			0f, 0f, textView.width.toFloat(), textView.height.toFloat(),
			startColor, endColor, Shader.TileMode.CLAMP
		)
		textView.paint.shader = shader
	}
}
