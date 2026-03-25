package com.fsacchi.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fsacchi.database.entity.WeatherHistoryEntity

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WeatherHistoryEntity)

    @Query("SELECT * FROM weather_history ORDER BY timestamp DESC")
    suspend fun getAll(): List<WeatherHistoryEntity>

    @Query("DELETE FROM weather_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM weather_history")
    suspend fun deleteAll()
}
