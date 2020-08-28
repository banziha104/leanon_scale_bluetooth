package com.opusone.leanon.test.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import io.reactivex.Completable

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(data: T): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertAll(data : List<T>) : Completable

    @Update
    abstract fun update(data: T): Completable

    @Delete
    abstract fun delete(data :T): Completable

}