package com.example.batani.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.batani.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [City::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lokasiDao(): LokasiDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "note_database"
                    )
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                CoroutineScope(Dispatchers.IO).launch {
                                    val jsonString = context.resources.openRawResource(R.raw.cities)
                                        .bufferedReader()
                                        .use { it.readText() }
                                    val gson = Gson()
                                    val cityListType = object : TypeToken<List<City>>() {}.type
                                    val cities: List<City> = gson.fromJson(jsonString, cityListType)

                                    INSTANCE?.let { database ->
                                        database.lokasiDao().apply {
                                            cities.forEach { insertCity(it) }
                                        }
                                    }
                                }
                            }
                        })
                        .build()
                }
            }
            return INSTANCE as AppDatabase
        }
    }
}
