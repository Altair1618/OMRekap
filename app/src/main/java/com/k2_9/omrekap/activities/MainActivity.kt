package com.k2_9.omrekap.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.k2_9.omrekap.R
import com.k2_9.omrekap.fragments.HomePageFragment
import com.k2_9.omrekap.fragments.ResultPageFragment

class MainActivity : AppCompatActivity(), ResultPageFragment.OnButtonClickListener {
	override fun onHomeButtonClick() {
		// Replace the current fragment with the home fragment
		supportFragmentManager.beginTransaction()
			.replace(R.id.fragment_container_view, HomePageFragment())
			.commit()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
	}
}
