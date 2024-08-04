package ca.unb.mobiledev.group18project.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ca.unb.mobiledev.group18project.daos.CourseDao
import ca.unb.mobiledev.group18project.daos.DeliverableDao
import ca.unb.mobiledev.group18project.entities.Course
import ca.unb.mobiledev.group18project.entities.Deliverable
import kotlin.jvm.Volatile
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Database layer in top of the SQLite database
 */
@Database(entities = [Course::class, Deliverable::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao?
    abstract fun deliverableDao(): DeliverableDao?

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val NUMBER_OF_THREADS = 4

        // Define an ExecutorService with a fixed thread pool which is used to run database operations asynchronously on a background thread
        val databaseWriterExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        // Singleton access to the database
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
//                val instance = Room.databaseBuilder(context.applicationContext,
//                    AppDatabase::class.java, "app_database")
//                    .build()

                val instance = Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}
