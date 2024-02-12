package com.lagradost.cloudstream3.utils

object VideoDownloadManager {
    interface IDownloadableMinimum {
        val url: String
        val referer: String
        val headers: Map<String, String>
    }
}