package ca.unb.mobiledev.group18project.ui.courses

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ca.unb.mobiledev.group18project.entities.Course
import ca.unb.mobiledev.group18project.repository.CourseRepository
import java.util.Date

class CoursesViewModel(application: Application) : AndroidViewModel(application){

    private val courseRepository: CourseRepository = CourseRepository(application)

    val allCourses: LiveData<List<Course>> = courseRepository.getAllRecords()

    // Insert a new record
    fun insert(name: String?, ch: Int, startDate: String?, endDate: String?, info: String) { //, startDate: Date, endDate: Date, info: String
        courseRepository.insertRecord(name, ch, startDate, endDate, info) //, startDate, endDate, info
    }

    /*fun deleteByName(name: String) {
        courseRepository.deleteRecordByName(name)
    }*/


    fun delete(course: Course) {
        courseRepository.deleteRecord(course)
    }

    fun update(course: Course) {
        courseRepository.updateRecord(course)
    }

    fun getAllIncompleteCourses(): LiveData<List<Course>> {
        return courseRepository.getAllIncompleteRecords()
    }

    fun getAllCompleteCourses(): LiveData<List<Course>> {
        return courseRepository.getAllCompletedRecords()
    }

    fun updatePastDates(currentDate: String) {
        courseRepository.updatePastDates(currentDate)
    }

    fun updateFutureDates(currentDate: String) {
        courseRepository.updateFutureDates(currentDate)
    }
}