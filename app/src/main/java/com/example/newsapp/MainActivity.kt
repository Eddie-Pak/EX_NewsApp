package com.example.newsapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.databinding.ActivityMainBinding
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import kotlin.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsAdapter

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.yna.co.kr/")
        .addConverterFactory(
            TikXmlConverterFactory.create(
                TikXml.Builder()
                    .exceptionOnUnreadXml(false)
                    .build()
            )
        ).build()

    // 검색을 하여 불러오는 주소가 연합뉴스에는 없어 사진이 안뜨더라도 구글을 통해 하려고 새로 만듬
    private val retrofitSearch = Retrofit.Builder()
        .baseUrl("https://news.google.com/")
        .addConverterFactory(
            TikXmlConverterFactory.create(
                TikXml.Builder()
                    .exceptionOnUnreadXml(false)
                    .build()
            )
        ).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsAdapter = NewsAdapter()

        val newsService = retrofit.create(NewsService::class.java)
        val searchService = retrofitSearch.create(NewsService::class.java)

        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }

        binding.feedChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.feedChip.isChecked = true

            newsService.mainFeed().submitList()
        }

        binding.politicsChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.politicsChip.isChecked = true

            newsService.politicsNews().submitList()
        }

        binding.industryChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.industryChip.isChecked = true

            newsService.industryNews().submitList()
        }

        binding.worldChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.worldChip.isChecked = true

            newsService.worldNews().submitList()
        }

        binding.cultureChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.cultureChip.isChecked = true

            newsService.cultureNews().submitList()
        }

        binding.sportChip.setOnClickListener {
            binding.chipGroup.clearCheck()
            binding.sportChip.isChecked = true

            newsService.sportsNews().submitList()
        }
        
        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.chipGroup.clearCheck()

                binding.searchEditText.clearFocus()

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

                searchService.search(binding.searchEditText.text.toString()).submitList()

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        newsService.mainFeed().submitList()
    }

    private fun Call<NewsRss>.submitList() {
        enqueue(object: Callback<NewsRss> {
            override fun onResponse(call: Call<NewsRss>, response: Response<NewsRss>) {

                val list = response.body()?.channel?.items.orEmpty().transform()
                newsAdapter.submitList(list)

                list.forEachIndexed { index, news ->
                    Thread {
                        try {
                            val jsoup = Jsoup.connect(news.link).get()
                            val elements = jsoup.select("meta[property^=og:]")
                            val ogImageNode = elements.find { node ->
                                node.attr("property") == "og:image"
                            }
                            news.imageUrl = ogImageNode?.attr("content")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        runOnUiThread {
                            newsAdapter.notifyItemChanged(index)
                        }
                    }.start()
                }
            }

            override fun onFailure(call: Call<NewsRss>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}