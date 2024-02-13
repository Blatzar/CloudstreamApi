package com.lagradost.cloudstreamtest

import com.lagradost.cloudstream3.AnimeLoadResponse
import com.lagradost.cloudstream3.HomePageResponse
import com.lagradost.cloudstream3.LiveStreamLoadResponse
import com.lagradost.cloudstream3.LoadResponse
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.MainPageRequest
import com.lagradost.cloudstream3.MovieLoadResponse
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.TvSeriesLoadResponse
import com.lagradost.cloudstream3.utils.ExtractorLink
import java.lang.RuntimeException

/**
 * Simple and easy testing class for providers.
 * Should really be expanded to be able to test all providers properly.
 * @see testAll
 */
open class ProviderTester(private val provider: MainAPI) {
    suspend fun testSearch(query: String, verbose: Boolean = false): List<SearchResponse> {
        val responses = provider.search(query) ?: emptyList()
        println("Response count: ${responses.size}, Query: $query")
        printSearchResponses(responses, verbose)
        return responses
    }

    private fun printSearchResponses(responses: List<SearchResponse>, verbose: Boolean) {
        if (verbose) {
            println("Responses:\n${responses.joinToString("\n")}")
        } else {
            println("Responses:\n${responses.map { it.name to it.url }.joinToString("\n")}")
        }
    }

    suspend fun testMainPage(verbose: Boolean = false): List<HomePageResponse> {
        if (!provider.hasMainPage) {
            throw RuntimeException("Provider does not have a main page!")
        }
        val responses = provider.mainPage.map { request ->
            provider.getMainPage(1, MainPageRequest(request.name, request.data, false))
        }.ifEmpty { listOf(provider.getMainPage(1, MainPageRequest("", "", false))) }
            .mapNotNull { it }

        responses.map { it.items }.flatten().forEach {
            println("Main page: ${it.name}, Item count: ${it.list.size}")
            printSearchResponses(it.list, verbose)
        }
        return responses
    }

    suspend fun testLoad(url: String): LoadResponse? {
        println("Loading response from: $url")
        val response = provider.load(url)
        println("Loaded response: $response")
        return response
    }

    suspend fun testLoadLinks(data: String): Pair<List<ExtractorLink>, List<SubtitleFile>> {
        val subtitles = mutableListOf<SubtitleFile>()
        val links = mutableListOf<ExtractorLink>()
        provider.loadLinks(data, false, { file ->
            subtitles.add(file)
        }, { link ->
            links.add(link)
        })

        println("Links count: ${links.size}, Subtitles count: ${subtitles.size}")
        println("Links: ${links.joinToString("\n")}")
        println("Subtitles: ${subtitles.joinToString("\n")}")

        return links to subtitles
    }

    /**
     * Picks an item on the mainPage or searches for an item. Using the item it tries to load the page and all links from the first episode.
     * @see testMainPage
     * @see testSearch
     * @see testLoad
     * @see testLoadLinks
     * @param query Query to use when searching, by default tries to use the first result from homepage.
     * @param mainPageItemIndex Index of item on homepage to pick, by default the first item is picked.
     * @param searchIndex Index of search result to pick if mainPage is not used.
     * @param loadUrl Override url of item to load(), by default uses what search or homepage supplies.
     * @param loadData Override the data supplied to loadLinks(), by default uses what load() was supplied with. See loadUrl.
     **/
    suspend fun testAll(
        query: String? = null,
        mainPageItemIndex: Int? = null,
        searchIndex: Int? = null,
        loadUrl: String? = null,
        loadData: String? = null
    ) {
        val response = if (provider.hasMainPage) {
            println("Testing Main Page: -------------------")
            val mainPage = testMainPage()
            val items = mainPage.flatMap { it.items }.flatMap { it.list }
            val item = mainPageItemIndex?.let { items.getOrNull(it) } ?: items.first()
            println("\n\nTesting Search: -------------------")

            val searchResponses = testSearch(query ?: item.name)
            assert(searchResponses.isNotEmpty())

            item
        } else if (query != null) {
            println("Testing Search: -------------------")

            val items = testSearch(query)
            val item = searchIndex?.let { items.getOrNull(it) } ?: items.first()
            item
        } else {
            throw RuntimeException("Cannot test everything without a query or a homepage")
        }
        println("\n\nTesting load: -------------------")

        val url = loadUrl ?: response.url
        val loadResponse = testLoad(url)
        assert(loadResponse != null)
        if (loadResponse!!.url != url) {
            println("Testing bookmark functionality")
            val secondResponse = testLoad(loadResponse.url)
            assert(secondResponse != null)
        }

        val data = loadData ?: when (loadResponse) {
            is AnimeLoadResponse -> {
                loadResponse.episodes.values.first().first().data
            }

            is MovieLoadResponse -> {
                loadResponse.dataUrl
            }

            is LiveStreamLoadResponse -> {
                loadResponse.dataUrl
            }

            is TvSeriesLoadResponse -> {
                loadResponse.episodes.first().data
            }

            else -> {
                throw RuntimeException("Unknown load response: ${loadResponse::class.simpleName}")
            }
        }

        println("\n\nTesting LoadLinks: -------------------")
        val (links, subs) = testLoadLinks(data)
    }
}