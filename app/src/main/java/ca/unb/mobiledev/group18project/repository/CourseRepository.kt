package ca.unb.mobiledev.group18project.repository

import android.app.Application
import androidx.lifecycle.LiveData
import ca.unb.mobiledev.group18project.daos.CourseDao
import ca.unb.mobiledev.group18project.db.AppDatabase
import ca.unb.mobiledev.group18project.db.AppDatabase.Companion.getDatabase
import ca.unb.mobiledev.group18project.entities.Course
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class CourseRepository(application: Application) {
    private val courseDao: CourseDao? = getDatabase(application).courseDao()

    fun insertRecord(name: String?, ch: Int, startDate: String?, endDate: String?, info: String) { //, startDate: Date, endDate: Date, info: String
        val course = Course()
        course.name = name
        course.ch = ch
        course.startDate = startDate
        course.endDate = endDate
        course.completed = false
        course.info = info
        AppDatabase.databaseWriterExecutor.execute { courseDao!!.insertCourse(course) }
    }

    fun updateRecord(course: Course) {
        AppDatabase.databaseWriterExecutor.execute { courseDao!!.updateCourse(course) }
    }

    /*fun deleteRecordByName(name: String) {
        AppDatabase.databaseWriterExecutor.execute { courseDao!!.deleteCourseByName(name) }
    }*/

    fun deleteRecord(course: Course) {
        AppDatabase.databaseWriterExecutor.execute { courseDao!!.deleteCourse(course) }
    }


    fun getAllRecords(): LiveData<List<Course>> {
        val searchResultFuture = Executors.newSingleThreadExecutor().submit(Callable {
            courseDao!!.listAllCourses()
        })
        return searchResultFuture.get()
    }

    fun getAllCompletedRecords(): LiveData<List<Course>> {
        val searchResultFuture = Executors.newSingleThreadExecutor().submit(Callable {
            courseDao!!.listAllCompletedCourses()
        })
        return searchResultFuture.get()
    }

    fun getAllIncompleteRecords(): LiveData<List<Course>> {
        val searchResultFuture = Executors.newSingleThreadExecutor().submit(Callable {
            courseDao!!.listAllIncompleteCourses()
        })
        return searchResultFuture.get()
    }

    fun updatePastDates(currentDate: String) {
        AppDatabase.databaseWriterExecutor.execute { courseDao!!.updatePastDates(currentDate) }
    }

    fun updateFutureDates(currentDate: String) {
        AppDatabase.databaseWriterExecutor.execute { courseDao!!.updateFutureDates(currentDate) }
    }
}