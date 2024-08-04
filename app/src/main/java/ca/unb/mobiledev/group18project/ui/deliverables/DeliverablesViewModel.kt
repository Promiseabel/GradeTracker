package ca.unb.mobiledev.group18project.ui.deliverables

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ca.unb.mobiledev.group18project.entities.Course
import ca.unb.mobiledev.group18project.entities.Deliverable
import ca.unb.mobiledev.group18project.repository.DeliverableRepository
import java.util.Date

class DeliverablesViewModel(application: Application) : AndroidViewModel(application){

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Deliverables Section"
    }
    val text: LiveData<String> = _text


    private val deliverableRepository: DeliverableRepository = DeliverableRepository(application)

    val allDeliverables: LiveData<List<Deliverable>> = deliverableRepository.getAllRecords()

    // Insert a new record
    fun insert(name: String?, courseName: String?, courseID: Int, dueDate: String?, dueTime: String?, weight: Int, info: String, grade: Int?) {
        deliverableRepository.insertRecord(name, courseName, courseID, dueDate, dueTime, weight, info, grade) //, dueDate, weight, info
    }

    fun delete(deliverable: Deliverable) {
        deliverableRepository.deleteRecord(deliverable)
    }

    fun update(deliverable: Deliverable) {
        deliverableRepository.updateRecord(deliverable)
    }

    fun getAllIncompleteDeliverables(): LiveData<List<Deliverable>> {
        return deliverableRepository.getAllIncompleteRecords()
    }

    fun getAllDeliverablesOfACourse(courseID: Int): LiveData<List<Deliverable>> {
        return deliverableRepository.getAllDeliverablesOfACourse(courseID)
    }

    fun updatePastDates(currentDateTime: String) {
        deliverableRepository.updatePastDates(currentDateTime)
    }

    fun updateFutureDates(currentDateTime: String) {
        deliverableRepository.updateFutureDates(currentDateTime)
    }

    suspend fun getCourse(courseId: Int): Course {
        return deliverableRepository.getCourse(courseId)
    }
}