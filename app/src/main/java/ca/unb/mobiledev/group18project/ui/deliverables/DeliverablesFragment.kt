package ca.unb.mobiledev.group18project.ui.deliverables

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.unb.mobiledev.group18project.R
import ca.unb.mobiledev.group18project.databinding.FragmentCoursesBinding
import ca.unb.mobiledev.group18project.databinding.FragmentDeliverablesBinding
import ca.unb.mobiledev.group18project.entities.Course
import ca.unb.mobiledev.group18project.entities.Deliverable
import ca.unb.mobiledev.group18project.ui.courses.CoursesAdapter
import ca.unb.mobiledev.group18project.ui.courses.CoursesViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DeliverablesFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDeliverablesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.

    private lateinit var mDeliverablesViewModel: DeliverablesViewModel
    private lateinit var mCourseViewModel: CoursesViewModel
    private lateinit var mListView: ListView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mDeliverablesViewModel = ViewModelProvider(this).get(DeliverablesViewModel::class.java)
        mCourseViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)

        _binding = FragmentDeliverablesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mListView = root.findViewById(R.id.deliverables_view)
        val addButton = root.findViewById<Button>(R.id.add_deliverables)

        addButton.setOnClickListener(this)

        mDeliverablesViewModel.getAllIncompleteDeliverables().observe(viewLifecycleOwner) {
            SearchIncompleteDeliverables()
        }

        updateDeliverables()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.add_deliverables -> {
                BuildDialog("ADD DELIVERABLE", null, true)
            }
        }
    }


    fun BuildDialog(title: String, deliverable: Deliverable?, new: Boolean) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_deliverable, null)
        val spinnerCourse = dialogView.findViewById<Spinner>(R.id.spinnerCourses)
        val editDeliverableName = dialogView.findViewById<EditText>(R.id.editTextDeliverableName)
        val editDeliverableWeight = dialogView.findViewById<EditText>(R.id.editTextDeliverableWeight)
        val editDeliverableGrade = dialogView.findViewById<EditText>(R.id.editTextDeliverableGrade)
        val dueDateButton = dialogView.findViewById<Button>(R.id.dueDate)
        val dueTimeButton = dialogView.findViewById<Button>(R.id.dueTime)
        val infoText = dialogView.findViewById<EditText>(R.id.infoTextView)

        var selectedDueDate = ""
        var selectedDueTime = ""

        dueDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            if (!new && deliverable?.dueDate != null) {
                val dueDateParts = deliverable.dueDate.toString().split("-")
                if (dueDateParts.size == 3) {
                    year = dueDateParts[0].toInt()  // Year
                    month = dueDateParts[1].toInt() - 1  // Month is 0-based in Calendar
                    day = dueDateParts[2].toInt()   // Day
                }
            }
            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                // Note: Month is 0-based, so add 1 for display
                selectedDueDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                dueDateButton.text = selectedDueDate
            }, year, month, day)

            datePickerDialog.show()
        }

        dueTimeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            var hour = calendar.get(Calendar.HOUR_OF_DAY)
            var minute = calendar.get(Calendar.MINUTE)

            if (!new && deliverable?.dueTime != null) {
                val dueTimeParts = deliverable.dueTime.toString().split(":")
                if (dueTimeParts.size == 2) {
                    hour = dueTimeParts[0].toInt()
                    minute = dueTimeParts[1].toInt()
                }
            }
            val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                selectedDueTime = String.format("%02d:%02d", hourOfDay, minute)
                dueTimeButton.text = selectedDueTime
            }, hour, minute, true)

            timePickerDialog.show()
        }

        val modifiedCourseList = mutableListOf<Course>()

        // Assuming you have a list of Course objects
        mCourseViewModel.getAllIncompleteCourses().observe(viewLifecycleOwner) { courses ->
            // courses is a non-null List<Course> here


            val placeHolder: Course = Course()
            placeHolder.name = "Select a Course"
            placeHolder.courseID = -1
            modifiedCourseList.add(placeHolder)
            modifiedCourseList.addAll(courses)

            val courseNames = modifiedCourseList.map { it.name }

            // Create an ArrayAdapter
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courseNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCourse.adapter = adapter

            if (!new) {
                var courseIndex = modifiedCourseList.indexOfFirst { it.courseID == deliverable?.courseID }
                if (courseIndex != -1) {
                    spinnerCourse.setSelection(courseIndex)
                }
            }
            // Update your ListView or UI component here with courseList
        }// get your list of courses here

        if(!new){
            // Set existing values
            editDeliverableName.setText(deliverable?.name)
            editDeliverableWeight.setText(deliverable?.weight.toString())
            if(deliverable?.grade != null){
                editDeliverableGrade.setText(deliverable?.grade.toString())
            }
            selectedDueDate = deliverable?.dueDate.toString() // Format: "YYYY-MM-DD"
            dueDateButton.text = selectedDueDate
            selectedDueTime = deliverable?.dueTime.toString() // Format: "HH:MM"
            if(!selectedDueTime.isNullOrEmpty()){
                dueTimeButton.text = selectedDueTime
            }
            infoText.setText(deliverable?.info.toString())
        }
        builder.setView(dialogView)
            .setTitle(title)
            .setPositiveButton("Submit") { _, _ ->

                try{
                    val cName = modifiedCourseList.get(spinnerCourse.selectedItemId.toInt()).name
                    val courseID = modifiedCourseList.get(spinnerCourse.selectedItemId.toInt()).courseID
                    val dName = editDeliverableName.text.toString()
                    val dWeight = editDeliverableWeight.text.toString().toInt()
                    var dGradeText = editDeliverableGrade.text.toString()
                    val info = infoText.text.toString()

                    var dGrade : Int? = null
                    if (dGradeText != ""){
                        dGrade = dGradeText.toInt()
                    }

                    if (cName == "Select a Course" || courseID == -1 || dName == "" || dWeight == null || selectedDueDate == "") {
                        Toast.makeText(binding.root.context, "Data entered is incomplete/incorrect format. Data has not been saved.", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    if (new) {
                        Toast.makeText(binding.root.context, "New Data Entry", Toast.LENGTH_SHORT).show()
                        mDeliverablesViewModel.insert(dName, cName, courseID, selectedDueDate, selectedDueTime, dWeight, info, dGrade)
                    } else {
                        Toast.makeText(binding.root.context, "Updated Data Entry", Toast.LENGTH_SHORT).show()
                        deliverable?.name = dName
                        deliverable?.courseName = cName
                        deliverable?.courseID  = courseID
                        deliverable?.info = info
                        deliverable?.dueDate = selectedDueDate
                        deliverable?.dueTime = selectedDueTime
                        deliverable?.weight = dWeight
                        deliverable?.grade = dGrade
                        mDeliverablesViewModel.update(deliverable!!)
                    }

                    updateDeliverables()
                } catch(e: Exception){
                    Toast.makeText(binding.root.context, "Something Went Wrong. Please ensure correct format", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
            }
            .setNegativeButton("Cancel", null)

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun SearchIncompleteDeliverables() {
        mDeliverablesViewModel.getAllIncompleteDeliverables().observe(viewLifecycleOwner) { deliverables ->
            val adapter = DeliverableAdapter(requireContext(), deliverables, mDeliverablesViewModel,this)
            mListView.adapter = adapter
        }
    }

    fun updateDeliverables(){
        //Temporary Placed here to update past dates
        val calendar = Calendar.getInstance()
        val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.time)

        mDeliverablesViewModel.updatePastDates(currentDateTime)
    }
}
