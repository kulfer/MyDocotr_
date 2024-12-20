package com.MyPobmor

// HistoryAdapter.kt
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.MyPobmor.HistoryAdapter.HistoryItem

class HistoryAdapter(private val context: Context, private val data: List<HistoryItem>) : BaseAdapter() {

    private class ViewHolder(view: View) {
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val typeTextView: TextView = view.findViewById(R.id.typeTextView)
        val statusTextView: TextView = view.findViewById(R.id.statusTextView)
        val detailtext: TextView = view.findViewById(R.id.detailtext)
    }

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): Any = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.history_item_layout, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val historyItem = getItem(position) as HistoryItem
        viewHolder.dateTextView.text = historyItem.date
        viewHolder.typeTextView.text = historyItem.type
        viewHolder.statusTextView.text = historyItem.status

        // Set up the details button click listener here
        viewHolder.detailtext.setOnClickListener {
            // Handle click event to show details
        }

        return view
    }
    // HistoryItem.kt
    data class HistoryItem(
        val date: String,
        val type: String,
        val status: String
    )
}