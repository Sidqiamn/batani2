package com.example.batani.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LokasiDao {
    @Query("SELECT code FROM city_table WHERE name = :cityName")
    suspend fun getCityCode(cityName: String): String?

    @Insert
        (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: City)

}
