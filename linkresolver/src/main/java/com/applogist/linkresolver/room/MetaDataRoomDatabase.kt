package com.applogist.linkresolver.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.applogist.linkresolver.MetaData
import java.util.concurrent.Executors

/*
*  Created by Mustafa Ürgüplüoğlu on 01.06.2020.
*  Copyright © 2020 Mustafa Ürgüplüoğlu. All rights reserved.
*/

@Database(entities = [MetaData::class], version = 1, exportSchema = false)
internal abstract class MetaDataRoomDatabase : RoomDatabase() {

    abstract fun metaDataDao(): MetaDataDao

    companion object {
        // marking the instance as volatile to ensure atomic access to the variable
        @Volatile
        private var INSTANCE: MetaDataRoomDatabase? = null
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        fun getDatabase(context: Context): MetaDataRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(MetaDataRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            MetaDataRoomDatabase::class.java, "linkresolver_metadata_database"
                        )
                            //.addCallback(sRoomDatabaseCallback)
                            .build()
                    }
                }
            }
            return INSTANCE
        }

    }
}