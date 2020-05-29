package com.applogist.linkresolver

/*
*  Created by Mustafa Ürgüplüoğlu on 13.05.2020.
*  Copyright © 2020 Mustafa Ürgüplüoğlu. All rights reserved.
*/

data class MetaData(
    var title : String = "",
    var type : String = "",
    var image : String = "",
    var url : String = "",
    var siteName : String = "",
    var description : String = "",

    var allMetaTags : HashMap<String, String> = hashMapOf()
){
    override fun toString(): String {
        return "title: $title\n" +
                "type: $type\n" +
                "url: $url\n" +
                "description: $description\n" +
                "siteName: $siteName\n" +
                "image: $image\n"
    }
}