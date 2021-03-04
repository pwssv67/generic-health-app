package ru.pwssv67.healthcounter.ui.adapters

import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import ru.pwssv67.healthcounter.R
import ru.pwssv67.healthcounter.models.News
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

class NewsAdapter(private val listener:(News)->Unit, val context: Context):RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var items: List<News> = listOf<News>()

    fun updateData(data: List <News>) {
        items = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.news_card, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }


    inner class NewsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var newsHeader: TextView? = null
        var newsBody: TextView? = null
        var newsDate: TextView? = null

        init {
            newsHeader = itemView.findViewById(R.id.tv_news_header)
            newsBody = itemView.findViewById(R.id.tv_news_body)
            newsDate = itemView.findViewById(R.id.tv_news_date)
        }

        fun bind(item: News, listener: (News) -> Unit) {
            newsHeader?.text = item.header
            newsDate?.text = SimpleDateFormat("dd/MM", Locale("ru")).format(item.date)
            newsBody?.text = item.text
            setListener(item, listener)
        }

        private fun setListener (item: News, listener: (News) -> Unit) {
            itemView.setOnLongClickListener {
                animateView(it, 150L)
                listener.invoke(item)
                return@setOnLongClickListener true
            }

            itemView.setOnClickListener{
                animateView(it)
            }
        }

        private fun animateView(view:View, duration: Long = 75L) {
            val elevationFrom = context.resources.getDimensionPixelSize(R.dimen.default_elevation).toFloat()
            val elevationTo = elevationFrom*1.5f
            val sizeFrom = 1.0f
            val sizeTo = 1.05f
            val elevationAnimator = ValueAnimator.ofFloat(elevationFrom, elevationTo)
            val sizeAnimator = ValueAnimator.ofFloat(sizeFrom, sizeTo)

            with(elevationAnimator) {
                this.duration = duration
                interpolator = FastOutSlowInInterpolator()
                repeatMode = ValueAnimator.REVERSE
                repeatCount = 1
                addUpdateListener {
                    view.elevation = it.animatedValue as Float
                }
            }

            with(sizeAnimator) {
                this.duration = duration
                repeatMode = ValueAnimator.REVERSE
                interpolator = FastOutLinearInInterpolator()
                repeatCount = 1
                addUpdateListener {
                    view.scaleX = it.animatedValue as Float
                    view.scaleY = it.animatedValue as Float
                }
            }

            elevationAnimator.start()
            sizeAnimator.start()
        }
    }
}