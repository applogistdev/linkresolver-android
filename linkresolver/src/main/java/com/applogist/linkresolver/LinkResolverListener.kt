package com.applogist.linkresolver

/*
*  Created by Mustafa Ürgüplüoğlu on 13.05.2020.
*  Copyright © 2020 Mustafa Ürgüplüoğlu. All rights reserved.
*/

interface LinkResolverListener{
    fun onSuccess(metaData: MetaData)
    fun onError(error: LinkResolverError)
}