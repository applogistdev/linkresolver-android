package com.applogist.linkresolver

import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Url


/*
*  Created by Mustafa Ürgüplüoğlu on 20.08.2020.
*  Copyright © 2020 Mustafa Ürgüplüoğlu. All rights reserved.
*/

interface ApiService {
    @GET()
    suspend fun getWebsite(@Url url: String): ResponseBody
}

class RetrofitService{
    companion object{
        var service : ApiService? = null
            get() {
                if(field == null){
                    val retrofit: Retrofit = Retrofit.Builder()
                        .baseUrl("https://www.google.com/")
                        .build()

                    service = retrofit.create(ApiService::class.java)
                }

                return field
            }
    }
}