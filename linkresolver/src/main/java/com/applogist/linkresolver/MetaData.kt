package com.applogist.linkresolver

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/*
*  Created by Mustafa Ürgüplüoğlu on 13.05.2020.
*  Copyright © 2020 Mustafa Ürgüplüoğlu. All rights reserved.
*/

@Entity(tableName = "linkresolver_metadata_table")
data class MetaData(
    @PrimaryKey
    @NonNull
    var rawLink : String = "",

    var title : String = "",
    var type : String = "",
    var image : String = "",
    var url : String = "",
    var siteName : String = "",
    var description : String = "",

    @Ignore
    var userValue : Any? = null
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