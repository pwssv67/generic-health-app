package ru.pwssv67.healthcounter.models

import java.net.URL
import java.util.*

class News(
    val header:String,
    val text:String,
    val date: Date = Date(),
    val source:String = "https://github.com/pwssv67/"
) {
    object Creator {
        fun createFromPost(post:Post):News {
            return News(post.title, post.body)
        }
    }
}