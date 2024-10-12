package com.hakanoren.guvendeapp

data class FAQItem(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false // Açık veya kapalı durumunu tutmak için
)