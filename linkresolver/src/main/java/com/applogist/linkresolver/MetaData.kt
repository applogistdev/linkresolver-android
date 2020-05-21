package com.applogist.linkresolver

/*
*  Created by Mustafa Ürgüplüoğlu on 13.05.2020.
*  Copyright © 2020 Mustafa Ürgüplüoğlu. All rights reserved.
*/

data class MetaData(
    var title : String = "",
    var type : LinkTypes = LinkTypes.WEBSITE,
    var typeDetail : String = "",
    var image : String = "",
    var url : String = "",

    var description : String = "",
    var locale : String = "",
    var siteName : String = "",
    var imageType : String = "",
    var imageWidth : String = "",
    var imageHeight : String = "",
    var video : String = "",
    var videoSecureUrl : String = "",
    var videoWidth : String = "",
    var videoHeight : String = "",
    var videoTags : ArrayList<String> = arrayListOf(),

    var audio : String = "",
    var audioType : String = "",
    var audioSecureUrl : String = "",

    var allMetaTags : HashMap<String, String> = hashMapOf()
){
    override fun toString(): String {
        return "title: $title\n" +
                "type: $type\n" +
                "typeDetail: $typeDetail\n" +

                "url: $url\n" +
                "description: $description\n" +
                "locale: $locale\n" +
                "siteName: $siteName\n" +

                "image: $image\n" +
                "imageType: $imageType\n" +
                "imageWidth: $imageWidth\n" +
                "imageHeight: $imageHeight\n" +

                "video: $video\n" +
                "videoWidth: $videoWidth\n" +
                "videoHeight: $videoHeight\n" +
                "videoTags: $videoTags\n" +

                "audio: $audio\n" +
                "audioType: $audioType\n" +
                "audioSecureUrl: $audioSecureUrl\n"
    }
}