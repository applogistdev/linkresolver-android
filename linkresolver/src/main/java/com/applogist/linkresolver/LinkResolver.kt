package com.applogist.linkresolver

import android.app.Application
import android.webkit.URLUtil
import com.applogist.linkresolver.room.MetaDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.UnsupportedMimeTypeException
import org.jsoup.nodes.Document

/*
*  Created by Mustafa Ürgüplüoğlu on 13.05.2020.
*  Copyright © 2020 Mustafa Ürgüplüoğlu. All rights reserved.
*/

class LinkResolver {
    companion object{
        lateinit var instance: LinkResolver

        fun init(application: Application, viewScope: CoroutineScope = CoroutineScope(Dispatchers.IO)){
            instance = LinkResolver()
            instance.repository = MetaDataRepository(application)
            instance.viewScope = viewScope
        }
    }
    private lateinit var repository: MetaDataRepository
    private lateinit var viewScope: CoroutineScope

    fun clearCache(){
        viewScope.launch {
            instance.repository.deleteAll()
        }
    }

    fun resolve(text : String, listener: LinkResolverListener, userValue: Any? = null) {
        val metaData = MetaData()
        metaData.userValue = userValue
        val links = text.extractLinks()
        if (links.isNotEmpty()) {
            metaData.rawLink = links[0]
            viewScope.launch {
                val exist = repository.getById(links[0])
                if (exist != null) {
                    exist.userValue = userValue
                    notifySuccess(exist, listener, false)
                } else {
                    val document: Document
                    try {
                        document = Jsoup.connect(links[0])
                            .userAgent("Mozilla")
                            .referrer("https://www.google.com")
                            .timeout(30 * 1000)
                            .followRedirects(true)
                            .get()
                    } catch (e: Exception) {
                        if (e is UnsupportedMimeTypeException) {
                            val mimeType = e.mimeType
                            if (mimeType != null && mimeType.startsWith("image")) {
                                metaData.image = links[0]
                                notifySuccess(metaData, listener)
                                return@launch
                            }
                        }
                        notifyError(LinkResolverError(e.message.toString()), listener)
                        return@launch
                    }

                    //css query https://www.w3schools.com/cssref/css_selectors.asp
                    //region Open Graph protocol https://ogp.me/
                    val ogAllTags = document.select("meta[property*=\"og:\"]")
                    val allTags = document.getElementsByTag("meta")

                    ogAllTags.forEach {
                        val property = it.attr("property")
                        val content = it.attr("content")
                        when (property) {
                            "og:title" -> {
                                metaData.title = content
                            }
                            "og:type" -> {
                                metaData.type = content
                            }
                            "og:image" -> {
                                if (URLUtil.isValidUrl(content)) {
                                    metaData.image = content
                                } else {
                                    metaData.image = links[0] + content
                                }
                            }
                            "og:url" -> {
                                metaData.url = content
                            }
                            "og:description" -> {
                                metaData.description = content
                            }
                        }
                    }
                    //endregion

                    allTags.forEach {
                        val name = it.attr("name")
                        val content = it.attr("content")

                        when (name) {
                            "title" -> {
                                if (metaData.title.isEmpty()) {
                                    metaData.title = content
                                }
                            }
                            "description" -> {
                                if (metaData.description.isEmpty()) {
                                    metaData.description = content
                                }
                            }
                        }
                    }

                    if (metaData.title.isEmpty() && metaData.siteName.isNotEmpty()) {
                        metaData.title = metaData.siteName
                    } else if (document.title().isNotEmpty()) {
                        metaData.title = document.title()
                    } else {
                        val title = document.toString().find("<title(.*?)>(.*?)</title>", 2)
                        if (title.isNotEmpty()) {
                            metaData.title = title
                        }
                    }

                    notifySuccess(metaData, listener)
                }
            }
        } else {
            listener.onError(LinkResolverError("URL not found"))
        }
    }

    private suspend fun notifySuccess(metaData: MetaData, listener: LinkResolverListener, save : Boolean = true) {
        if (metaData.url.isEmpty()) {
            metaData.url = metaData.rawLink
        }
        if(save){
            repository.insert(metaData)
        }
        withContext(Dispatchers.Main) {
            listener.onSuccess(metaData)
        }
    }

    private suspend fun notifyError(linkResolverError: LinkResolverError, listener: LinkResolverListener) {
        withContext(Dispatchers.Main) {
            listener.onError(linkResolverError)
        }
    }
}