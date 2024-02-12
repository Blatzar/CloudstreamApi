package com.lagradost.cloudstream3.plugins

import kotlin.Throws
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.APIHolder
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.extractorApis
import android.util.Log
import com.fasterxml.jackson.annotation.JsonProperty

const val PLUGIN_TAG = "PluginInstance"

abstract class Plugin {
    /**
     * Called when your Plugin is loaded
     * @param context Context
     */
    @Throws(Throwable::class)
    open fun load(context: Any) {
    }

    /**
     * Called when your Plugin is being unloaded
     */
    @Throws(Throwable::class)
    open fun beforeUnload() {
    }

    /**
     * Used to register providers instances of MainAPI
     * @param element MainAPI provider you want to register
     */
    fun registerMainAPI(element: MainAPI) {
        Log.i(PLUGIN_TAG, "Adding ${element.name} (${element.mainUrl}) MainAPI")
        // Race condition causing which would case duplicates if not for distinctBy
        synchronized(APIHolder.allProviders) {
            APIHolder.allProviders.add(element)
        }
        APIHolder.addPluginMapping(element)
    }

    /**
     * Used to register extractor instances of ExtractorApi
     * @param element ExtractorApi provider you want to register
     */
    fun registerExtractorAPI(element: ExtractorApi) {
        Log.i(PLUGIN_TAG, "Adding ${element.name} (${element.mainUrl}) ExtractorApi")
        extractorApis.add(element)
    }

    class Manifest {
        @JsonProperty("name")
        var name: String? = null
        @JsonProperty("pluginClassName")
        var pluginClassName: String? = null
        @JsonProperty("version")
        var version: Int? = null
        @JsonProperty("requiresResources")
        var requiresResources: Boolean = false
    }
}