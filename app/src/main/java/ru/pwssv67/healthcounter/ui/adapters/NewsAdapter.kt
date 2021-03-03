package ru.pwssv67.healthcounter.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.news_card.view.*
import ru.pwssv67.healthcounter.R

class NewsAdapter(private val values: List<Pair<String, String>>):RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.news_card, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.newsHeader?.text = values[position].first
        holder.newsBody?.text = values[position].second
        holder.newsDate?.text = "25.02"
    }

    class NewsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var newsHeader: TextView? = null
        var newsBody: TextView? = null
        var newsDate: TextView? = null

        init {
            newsHeader = itemView.findViewById(R.id.tv_news_header)
            newsBody = itemView.findViewById(R.id.tv_news_body)
            newsDate = itemView.findViewById(R.id.tv_news_date)
        }
    }
}