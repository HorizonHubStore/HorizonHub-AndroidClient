package com.example.horizonhub_androidclient.data.auth

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AuthStateDao {
    @Query("SELECT * FROM auth_state WHERE id = 1")
    fun getAuthState(): LiveData<AuthState?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAuthState(authStateEntity: AuthState)
}
