package com.applogist.linkresolver

import android.util.Patterns
import java.util.regex.Matcher
import java.util.regex.Pattern

/*
*  Created by Mustafa Ürgüplüoğlu on 13.05.2020.
*  Copyright © 2020 Mustafa Ürgüplüoğlu. All rights reserved.
*/

fun String.extractLinks(): ArrayList<String> {
    val links = arrayListOf<String>()
    val m: Matcher = Patterns.WEB_URL.matcher(this)
    while (m.find()) {
        val url: String = m.group()
        links.add(url)
    }
    return links
}

fun String.find(pattern: String, index: Int): String {
    var match = ""
    val matcher = Pattern.compile(pattern).matcher(this)
    while (matcher.find()) {
        match = matcher.group(index) ?: ""
        break
    }
    return match
}