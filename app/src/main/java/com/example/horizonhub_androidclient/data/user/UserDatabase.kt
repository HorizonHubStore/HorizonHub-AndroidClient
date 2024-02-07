package com.example.horizonhub_androidclient.data.user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.horizonhub_androidclient.data.auth.AuthState
import com.example.horizonhub_androidclient.data.auth.AuthStateDao

@Database(entities = [User::class, AuthState::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun authStateDao(): AuthStateDao


    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null


        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
