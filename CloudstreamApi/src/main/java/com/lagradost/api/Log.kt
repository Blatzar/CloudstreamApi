package com.lagradost.api

object Log {
    fun d(tag: String, message: String) {
        println("DEBUG $tag: $message")
    }

    fun i(tag: String, message: String) {
        println("INFO $tag: $message")
    }

    fun w(tag: String, message: String) {
        println("WARNING $tag: $message")
    }

    fun e(tag: String, message: String) {
        println("ERROR $tag: $message")
    }
}