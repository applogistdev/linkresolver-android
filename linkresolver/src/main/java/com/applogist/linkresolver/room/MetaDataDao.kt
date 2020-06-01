package com.applogist.linkresolver.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.applogist.linkresolver.MetaData

/*
*  Created by Mustafa Ürgüplüoğlu on 01.06.2020.
*  Copyright © 2020 Mustafa Ürgüplüoğlu. All rights reserved.
*/

@Dao
interface MetaDataDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(metaData: MetaData)

    @Query("SELECT * FROM linkresolver_metadata_table WHERE rawLink=:rawLink")
    fun getItemById(rawLink : String) : MetaData?

    @Query("DELETE FROM linkresolver_metadata_table")
    fun deleteAll()
}