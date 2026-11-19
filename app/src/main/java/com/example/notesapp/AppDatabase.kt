package com.example.notesapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Note::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migrasi database versi 1 ke versi 2

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
                CREATE TABLE notes_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    title TEXT NOT NULL,
                    note TEXT NOT NULL
                    )
                """)

                db.execSQL("""
                    INSERT INTO notes_new (id, title, note)
                    SELECT id, title, '' FROM notes
                """)

                db.execSQL("DROP TABLE notes")

                db.execSQL("ALTER TABLE notes_new RENAME TO notes")
            }
        }


        fun getInstance(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
