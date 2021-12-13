package com.example.smiline.model.db

import android.content.Context
import androidx.room.*


@Entity(tableName = "courses")
data class Course(@PrimaryKey val coourse_id: Int, val course_name: String, val course_url: String)
@Dao
interface UserDao {
    @Insert
    fun insert(user : Course)

    @Update
    fun update(user : Course)

    @Delete
    fun delete(user : Course)

    @Query("delete from courses")
    fun deleteAll()

    @Query("select * from courses")
    fun getAll(): List<Course>
}
@Database(entities = arrayOf(Course::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    /*
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }*/
}
