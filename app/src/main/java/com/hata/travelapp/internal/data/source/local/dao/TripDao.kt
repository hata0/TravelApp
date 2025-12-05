package com.hata.travelapp.internal.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hata.travelapp.internal.data.source.local.entity.TripEntity

/**
 * `trips`テーブルへのアクセスを定義するData Access Object。
 */
@Dao
interface TripDao {

    @Query("SELECT * FROM trips")
    suspend fun getTrips(): List<TripEntity>

    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getTripById(id: String): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :id")
    suspend fun deleteTrip(id: String)
}
