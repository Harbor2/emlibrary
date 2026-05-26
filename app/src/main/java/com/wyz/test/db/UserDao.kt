package com.wyz.test.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM user ORDER BY id DESC")
    fun getUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE name = :name")
    suspend fun getUserByName(name: String): UserEntity?

    @Query("SELECT * FROM user ORDER BY id DESC LIMIT 1")
    fun getLastUser(): LiveData<UserEntity?>

    @Query("DELETE FROM user WHERE id = :id")
    suspend fun deleteUserById(id: Int)
}