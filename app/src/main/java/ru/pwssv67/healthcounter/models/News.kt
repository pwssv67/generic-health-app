package ru.pwssv67.healthcounter.models

import java.net.URL
import java.util.*

data class News(
    val header:String,
    val text:String,
    val date: Date = Date(),
    val source:String = "https://github.com/pwssv67/"
) {
}