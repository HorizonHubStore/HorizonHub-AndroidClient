package com.example.horizonhub_androidclient.data.gamePost

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GamePost::class], version = 1, exportSchema = false)
abstract class GamePostDatabase : RoomDatabase() {

    abstract fun GamePostDao(): GamePostDao

    companion object {
        @Volatile
        private var INSTANCE: GamePostDatabase? = null

        fun getDatabase(context: Context): GamePostDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GamePostDatabase::class.java,
                    "game_post_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
