package com.example.newsapp

import retrofit2.Call
import retrofit2.http.GET

interface NewsService {
    @GET("rss/news.xml")
    fun mainFeed(): Call<NewsRss>

    @GET("rss/politics.xml")
    fun politicsNews(): Call<NewsRss>

    @GET("rss/industry.xml")
    fun industryNews(): Call<NewsRss>

    @GET("rss/international.xml")
    fun worldNews(): Call<NewsRss>

    @GET("rss/culture.xml")
    fun cultureNews(): Call<NewsRss>

    @GET("rss/sports.xml")
    fun sportsNews(): Call<NewsRss>
}