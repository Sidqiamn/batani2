package com.example.batani.database

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "city_table")
data class City(
    @PrimaryKey val name: String,
    val code: String
)
