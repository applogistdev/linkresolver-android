package com.applogist.linkresolver

import android.webkit.URLUtil
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

class LinkResolver(
    private val text: String,
    private val listener: LinkResolverListener,
    private val userValue : Any? = null,
    private val viewScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private var links = arrayListOf<String>()
    fun resolve() {
        val metaData = MetaData()
        metaData.userValue = userValue
        links = text.extractLinks()
        if (links.isNotEmpty()) {
            viewScope.launch {
                val document: Document
                try {
                    document = Jsoup.connect(links[0])
                        .userAgent("Mozilla")
                        .referrer("https://www.google.com")
                        .timeout(30 * 1000)
                        .followRedirects(true)
                        .get()
                }catch (e : Exception){
                    if (e is UnsupportedMimeTypeException) {
                        val mimeType = e.mimeType
                        if (mimeType != null && mimeType.startsWith("image")) {
                            metaData.image = links[0]
                            notifySuccess(metaData)
                            return@launch
                        }
                    }
                    notifyError(LinkResolverError(e.message.toString()))
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
                            if(URLUtil.isValidUrl(content)){
                                metaData.image = content
                            }else{
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
                    val property = it.attr("property")
                    val content = it.attr("content")
                    metaData.allMetaTags[if(property.isEmpty()) name else property] = content

                    when(name){
                        "title" -> {
                            if(metaData.title.isEmpty()){
                                metaData.title = content
                            }
                        }
                        "description" -> {
                            if(metaData.description.isEmpty()){
                                metaData.description = content
                            }
                        }
                    }
                }

                if(metaData.title.isEmpty() && metaData.siteName.isNotEmpty()){
                    metaData.title = metaData.siteName
                }else if(document.title().isNotEmpty()){
                    metaData.title = document.title()
                }else{
                    val title = document.toString().find("<title(.*?)>(.*?)</title>", 2)
                    if(title.isNotEmpty()){
                        metaData.title = title
                    }
                }

                notifySuccess(metaData)
            }
        } else {
            listener.onError(LinkResolverError("URL not found"))
        }
    }

    private suspend fun notifySuccess(metaData: MetaData) {
        if(metaData.url.isEmpty()){
            metaData.url = links[0]
        }
        withContext(Dispatchers.Main){
            listener.onSuccess(metaData)
        }
    }

    private suspend fun notifyError(linkResolverError: LinkResolverError) {
        withContext(Dispatchers.Main){
            listener.onError(linkResolverError)
        }
    }
}