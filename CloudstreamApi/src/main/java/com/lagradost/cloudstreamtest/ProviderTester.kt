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

    suspend fun testAll(query: String? = null) {
        val response = if (provider.hasMainPage) {
            println("Testing Main Page: -------------------")
            val mainPage = testMainPage()
            val item = mainPage.first().items.first().list.first()
            println("\n\nTesting Search: -------------------")

            val searchResponses = testSearch(item.name)
            assert(searchResponses.isNotEmpty())

            item
        } else if (query != null) {
            println("Testing Search: -------------------")

            testSearch(query).first()
        } else {
            throw RuntimeException("Cannot test everything without a query or a homepage")
        }
        println("\n\nTesting load: -------------------")

        val loadResponse = testLoad(response.url)
        assert(loadResponse != null)
        if (loadResponse!!.url != response.url) {
            println("Testing bookmark functionality")
            val secondResponse = testLoad(loadResponse.url)
            assert(secondResponse != null)
        }

        val data = when (loadResponse) {
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