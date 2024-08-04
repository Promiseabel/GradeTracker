package ca.unb.mobiledev.group18project.ui.courses

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ca.unb.mobiledev.group18project.GradeCalculations
import ca.unb.mobiledev.group18project.R
import ca.unb.mobiledev.group18project.databinding.FragmentCoursesBinding
import ca.unb.mobiledev.group18project.entities.Course
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

open class CoursesFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentCoursesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mCourseViewModel: CoursesViewModel
    private lateinit var mListView: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mCourseViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)

        _binding = FragmentCoursesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val historyButton = binding.coursesHistory

        historyButton.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_courses_to_navigation_courses_history)
        }

        mListView = root.findViewById(R.id.courses_view)

        val addButton = root.findViewById<Button>(R.id.add_course)

        addButton.setOnClickListener(this)

        updatePastClasses()

        retrieveGPA()

        mCourseViewModel.getAllIncompleteCourses().observe(viewLifecycleOwner) {
            SearchIncompleteCourses()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.add_course -> {
                BuildDialog("ADD COURSE", null, true)
            }
        }
    }

    open fun BuildDialog(title: String, course: Course?, new: Boolean) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_course, null)
        val editTextCourse = dialogView.findViewById<EditText>(R.id.editTextCourseName)
        val chTextCourse = dialogView.findViewById<EditText>(R.id.editTextCourseCH)
        val startDateButton = dialogView.findViewById<Button>(R.id.startDate)
        val endDateButton = dialogView.findViewById<Button>(R.id.endDate)
        val infoText = dialogView.findViewById<EditText>(R.id.infoTextView)

        var selectedStartDate = ""
        var selectedEndDate = ""

        startDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            if (!new && course?.startDate != null) {
                val dueDateParts = course.startDate.toString().split("-")
                if (dueDateParts.size == 3) {
                    year = dueDateParts[0].toInt()  // Year
                    month = dueDateParts[1].toInt() - 1  // Month is 0-based in Calendar
                    day = dueDateParts[2].toInt()   // Day
                }
            }
            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                // Note: Month is 0-based, so add 1 for display
                selectedStartDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                startDateButton.text = selectedStartDate
            }, year, month, day)

            datePickerDialog.show()
        }

        endDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            if (!new && course?.endDate != null) {
                val dueDateParts = course.endDate.toString().split("-")
                if (dueDateParts.size == 3) {
                    year = dueDateParts[0].toInt()  // Year
                    month = dueDateParts[1].toInt() - 1  // Month is 0-based in Calendar
                    day = dueDateParts[2].toInt()   // Day
                }
            }
            val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                // Note: Month is 0-based, so add 1 for display
                selectedEndDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                endDateButton.text = selectedEndDate
            }, year, month, day)

            datePickerDialog.show()
        }


        if(!new){
            // Set existing values
            editTextCourse.setText(course?.name)
            chTextCourse.setText(course?.ch.toString())
            selectedStartDate = course?.startDate.toString() // Format: "YYYY-MM-DD"
            startDateButton.text = selectedStartDate
            selectedEndDate = course?.endDate.toString() // Format: "YYYY-MM-DD"
            endDateButton.text = selectedEndDate
            infoText.setText(course?.info.toString())
        }

        builder.setView(dialogView)
            .setTitle(title)
            .setPositiveButton("Submit") { _, _ ->

                try{
                    val name = editTextCourse.text.toString()
                    val ch = chTextCourse.text.toString()
                    val info = infoText.text.toString()

                    if (name == "" || ch.toIntOrNull() == null || selectedStartDate == "" || selectedEndDate == "") {
                        Toast.makeText(binding.root.context, "Data entered is incomplete/incorrect format. Data has not been saved.", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    if (new) {
                        Toast.makeText(binding.root.context, "New Data Entry", Toast.LENGTH_SHORT).show()
                        mCourseViewModel.insert(name, ch.toInt(), selectedStartDate, selectedEndDate, info)
                    } else {
                        Toast.makeText(binding.root.context, "Updated Data Entry", Toast.LENGTH_SHORT).show()
                        course?.name = name
                        course?.ch = ch.toInt()
                        course?.startDate = selectedStartDate
                        course?.endDate = selectedEndDate
                        course?.info = info
                        mCourseViewModel.update(course!!)
                    }
                    updatePastClasses()
                } catch(e: Exception){
                    Toast.makeText(binding.root.context, "Something Went Wrong. Please ensure correct format", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
            }
            .setNegativeButton("Cancel", null)

                val dialog: AlertDialog = builder.create()
                dialog.show()
    }

    fun SearchIncompleteCourses() {
        mCourseViewModel.getAllIncompleteCourses().observe(viewLifecycleOwner) { courses ->
            val adapter = CoursesAdapter(requireContext(), courses, mCourseViewModel, this)
            mListView.adapter = adapter

            updateGPA(courses)
        }
    }

    fun updatePastClasses(){
        //Temporary Placed here to update past dates
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(calendar.time)

        mCourseViewModel.updatePastDates(currentDate)
    }

    open fun updateGPA(courses: List<Course>){
        var gpaText = binding.root.findViewById<TextView>(R.id.gpa)

        // Calculate GPA
        val result = GradeCalculations.calculateCumulativeGPA(courses)
        val resultText = String.format("%.1f", result)

        // Save GPA to Shared Preferences
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putFloat("GPA", result.toFloat())
            apply()
        }

        // Update UI
        gpaText.text = "GPA: $resultText"
    }

    open fun retrieveGPA() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val savedGPA = sharedPref?.getFloat("GPA", 0.0f) ?: 0.0f
        val formattedGPA = String.format("%.1f", savedGPA)

        // Assuming you have a TextView to display the GPA
        val gpaText = binding.root.findViewById<TextView>(R.id.gpa)
        gpaText.text = "GPA: $formattedGPA"
    }
}