package ru.pwssv67.healthcounter.ui.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_news.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.pwssv67.healthcounter.R
import ru.pwssv67.healthcounter.models.News
import ru.pwssv67.healthcounter.models.Post
import ru.pwssv67.healthcounter.network.NetworkService
import ru.pwssv67.healthcounter.ui.adapters.NewsAdapter
import java.util.*
import kotlin.collections.ArrayList

class NewsActivity : AppCompatActivity() {
    private lateinit var newsAdapter: NewsAdapter
    private var posts = MutableLiveData<MutableList<Post>>()
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
            layoutManager = LinearLayoutManager(this@NewsActivity)
            posts.observe(this@NewsActivity) { mutableList ->
                newsAdapter.updateData(mutableList.map{News.Creator.createFromPost(it)}.toList())
            }
            getFakeNews()
        }
    }

    private fun getFakeNews() {
        NetworkService.postsApi.getData().enqueue(
            object:Callback<MutableList<Post>> {
                override fun onResponse(
                    call: Call<MutableList<Post>>,
                    response: Response<MutableList<Post>>
                ) {
                    posts.postValue(response.body())
                }

                override fun onFailure(call: Call<MutableList<Post>>, t: Throwable) {
                    Log.e("NewsActivity", "postsApi is screwed")
                }
            }
        )
    }
}