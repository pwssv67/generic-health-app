package ru.pwssv67.healthcounter.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_news.*
import ru.pwssv67.healthcounter.R
import ru.pwssv67.healthcounter.ui.adapters.NewsAdapter

class NewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        with (rv_news) {
            adapter = NewsAdapter(createFakeNews())
            layoutManager = LinearLayoutManager(this@NewsActivity)
        }
    }

    private fun createFakeNews(): List<Pair<String, String>> {
        val list = ArrayList<Pair<String,String>>()
        for (i in 0..10) {
            list.add("#$i Fake News" to getString(R.string.lorem_ipsum))
        }
        return list.toList()
    }
}