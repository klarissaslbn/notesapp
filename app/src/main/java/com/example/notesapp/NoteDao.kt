package com.example.notesapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    // READ: mengambil seluruh catatan
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAll(): Flow<List<Note>>

    // CREATE: menambahkan catatan
    @Insert
    suspend fun insert(note: Note)

    // UPDATE: mengubah catatan
    @Update
    suspend fun update(note: Note)

    // DELETE: menghapus catatan
    @Delete
    suspend fun delete(note: Note)
}
