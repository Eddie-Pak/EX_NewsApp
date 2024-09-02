package com.example.newsapp

import retrofit2.Call
import retrofit2.http.GET

interface NewsService {
    @GET("rss/news.xml")
    fun mainFeed(): Call<NewsRss>
}