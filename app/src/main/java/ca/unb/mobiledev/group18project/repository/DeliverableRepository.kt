package ca.unb.mobiledev.group18project.repository

import android.app.Application
import androidx.lifecycle.LiveData
import ca.unb.mobiledev.group18project.daos.DeliverableDao
import ca.unb.mobiledev.group18project.db.AppDatabase
import ca.unb.mobiledev.group18project.db.AppDatabase.Companion.getDatabase
import ca.unb.mobiledev.group18project.entities.Course
import ca.unb.mobiledev.group18project.entities.Deliverable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class DeliverableRepository(application: Application) {
    private val deliverableDao: DeliverableDao? = getDatabase(application).deliverableDao()

    fun insertRecord(name: String?, courseName: String?, courseID: Int, dueDate: String?, dueTime: String?, weight: Int, info: String, grade: Int?) {
        val deliverable = Deliverable()
        deliverable.name = name
        deliverable.courseName = courseName
        deliverable.courseID = courseID
        deliverable.dueDate = dueDate
        deliverable.weight = weight
        deliverable.dueTime = dueTime
        deliverable.completed = false
        deliverable.info = info
        deliverable.grade = grade
        AppDatabase.databaseWriterExecutor.execute { deliverableDao!!.insertDeliverable(deliverable) }
    }

    fun updateRecord(deliverable: Deliverable) {
        AppDatabase.databaseWriterExecutor.execute { deliverableDao!!.updateDeliverable(deliverable) }
    }

    fun deleteRecord(deliverable: Deliverable) {
        AppDatabase.databaseWriterExecutor.execute { deliverableDao!!.deleteDeliverable(deliverable) }
    }


    fun getAllRecords(): LiveData<List<Deliverable>> {
        val searchResultFuture = Executors.newSingleThreadExecutor().submit(Callable {
            deliverableDao!!.listAllDeliverables()
        })
        return searchResultFuture.get()
    }

    fun getAllIncompleteRecords(): LiveData<List<Deliverable>> {
        val searchResultFuture = Executors.newSingleThreadExecutor().submit(Callable {
            deliverableDao!!.listAllIncompleteDeliverables()
        })
        return searchResultFuture.get()
    }

    fun getAllDeliverablesOfACourse(courseID: Int): LiveData<List<Deliverable>> {
        val searchResultFuture = Executors.newSingleThreadExecutor().submit(Callable {
            deliverableDao!!.listAllCourseDeliverables(courseID)
        })
        return searchResultFuture.get()
    }

    fun updatePastDates(currentDateTime: String) {
        AppDatabase.databaseWriterExecutor.execute { deliverableDao!!.updatePastDates(currentDateTime) }
    }

    fun updateFutureDates(currentDateTime: String) {
        AppDatabase.databaseWriterExecutor.execute { deliverableDao!!.updateFutureDates(currentDateTime) }
    }

    suspend fun getCourse(courseID: Int): Course {
        return withContext(Dispatchers.IO) {
            deliverableDao!!.getCourse(courseID)[0]
        }
    }
}