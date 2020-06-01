package com.applogist.linkresolver.room

import android.app.Application
import com.applogist.linkresolver.MetaData

/*
*  Created by Mustafa Ürgüplüoğlu on 01.06.2020.
*  Copyright © 2020 Mustafa Ürgüplüoğlu. All rights reserved.
*/

internal class MetaDataRepository(application: Application) {

    private val dao: MetaDataDao

    fun insert(metaData: MetaData) {
        MetaDataRoomDatabase.databaseWriteExecutor.execute { dao.insert(metaData) }
    }

    fun getById(rawLink: String): MetaData? {
        return dao.getItemById(rawLink)
    }

    fun deleteAll(){
        dao.deleteAll()
    }

    init {
        val db = MetaDataRoomDatabase.getDatabase(application)
        dao = db!!.metaDataDao()
    }
}