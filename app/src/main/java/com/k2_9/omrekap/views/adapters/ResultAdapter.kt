package com.k2_9.omrekap.views.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.k2_9.omrekap.R

class ResultAdapter(private val dataList: List<Pair<String, String>>) :
	RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {
	inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val keyTextView: TextView = itemView.findViewById(R.id.result_candidate_text)
		val valueTextView: TextView = itemView.findViewById(R.id.result_count_text)
	}

	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int,
	): ResultViewHolder {
		val view =
			LayoutInflater.from(parent.context)
				.inflate(R.layout.layout_result_row, parent, false)
		return ResultViewHolder(view)
	}

	override fun onBindViewHolder(
        holder: ResultViewHolder,
        position: Int,
	) {
		val item = dataList[position]
		holder.keyTextView.text = item.first
		holder.valueTextView.text = item.second
	}

	override fun getItemCount(): Int {
		return dataList.size
	}
}
