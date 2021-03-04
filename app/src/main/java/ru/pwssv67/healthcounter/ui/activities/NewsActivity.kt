package ru.pwssv67.healthcounter.ui.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_news.*
import ru.pwssv67.healthcounter.R
import ru.pwssv67.healthcounter.models.News
import ru.pwssv67.healthcounter.ui.adapters.NewsAdapter
import java.util.*
import kotlin.collections.ArrayList

class NewsActivity : AppCompatActivity() {
    private lateinit var newsAdapter: NewsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        with (rv_news) {
            newsAdapter = NewsAdapter ({
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.source))
                startActivity(intent)
            },
            context)

            adapter = newsAdapter
            newsAdapter.updateData(createFakeNews())
            layoutManager = LinearLayoutManager(this@NewsActivity)
        }
    }

    private fun createFakeNews(): List<News> {
        val list = ArrayList<News>()
        for (i in 0..10) {
            list.add(News("#$i Fake news", getString(R.string.lorem_ipsum), Date()))
        }
        return list.toList()
    }
}