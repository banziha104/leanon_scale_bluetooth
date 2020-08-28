package com.opusone.leanon.test.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.opusone.leanon.test.database.entity.DeviceEntity
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

@Dao
abstract class DeviceDao : BaseDao<DeviceEntity>{

    @Query("select * from qn_device ")
    abstract fun findAll() : Single<List<DeviceEntity>>

    fun insertOrUpdate(deviceEntity: DeviceEntity) : Completable {
        val data = findAll()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .blockingGet()
            .firstOrNull { it.id == 1L }
        return if (data == null){
            insert(deviceEntity)
        }else{
            update(deviceEntity)
        }
    }
}