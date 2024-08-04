package ca.unb.mobiledev.group18project.ui.singlecourse

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ca.unb.mobiledev.group18project.GradeCalculations
import ca.unb.mobiledev.group18project.MainActivity
import ca.unb.mobiledev.group18project.R
import ca.unb.mobiledev.group18project.databinding.FragmentSingleCourseBinding
import ca.unb.mobiledev.group18project.entities.Course
import ca.unb.mobiledev.group18project.entities.Deliverable
import ca.unb.mobiledev.group18project.ui.courses.CoursesViewModel
import ca.unb.mobiledev.group18project.ui.deliverables.DeliverableAdapter
import ca.unb.mobiledev.group18project.ui.deliverables.DeliverablesViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class SingleCourseFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentSingleCourseBinding? = null

    private lateinit var mDeliverablesViewModel: DeliverablesViewModel
    private lateinit var mCourseViewModel: CoursesViewModel
    private lateinit var mListView: ListView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var actionBar : ActionBar

    private lateinit var thisCourse : Course

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mDeliverablesViewModel = ViewModelProvider(this).get(DeliverablesViewModel::class.java)
        mCourseViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)

        _binding = FragmentSingleCourseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        arguments?.getSerializable("course")?.let { course ->
            if (course is Course) { // Replace 'Course' with the actual type of your course object
                thisCourse = course
            }
        }

        (activity as? AppCompatActivity)?.supportActionBar?.title = thisCourse.name

        mDeliverablesViewModel.getAllDeliverablesOfACourse(thisCourse.courseID).observe(viewLifecycleOwner) {
            SearchCourseDeliverables(thisCourse.courseID)
        }

        mListView = root.findViewById(R.id.deliverable_list)
        val addButton = root.findViewById<Button>(R.id.add_deliverables)

        refresh(root)

        addButton.setOnClickListener(this)



        return root
    }

    override fun onResume() {
        super.onResume()
        // Hide the action bar
        (activity as? MainActivity)?.hideBottomNav()
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showBottomNav()
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
        val dueDateButton = dialogView.findViewById<Button>(R.id.dueDate)
        val dueTimeButton = dialogView.findViewById<Button>(R.id.dueTime)
        val infoText = dialogView.findViewById<EditText>(R.id.infoTextView)
        val gradeText = dialogView.findViewById<EditText>(R.id.editTextDeliverableGrade)

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

        modifiedCourseList.add(thisCourse)

        val courseNames = modifiedCourseList.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courseNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourse.adapter = adapter

        spinnerCourse.isEnabled = false

        if(!new){
            // Set existing values
            editDeliverableName.setText(deliverable?.name)
            editDeliverableWeight.setText(deliverable?.weight.toString())
            if(deliverable?.grade != null) {
                gradeText.setText(deliverable?.grade.toString())
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
                    val info = infoText.text.toString()
                    val dGradeText = gradeText.text.toString()

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
                        deliverable?.grade= dGrade

                        mDeliverablesViewModel.update(deliverable!!)
                    }
                    refresh(binding.root)
                } catch(e: Exception){
                Toast.makeText(binding.root.context, "Something Went Wrong. Please ensure correct format", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            }
            .setNegativeButton("Cancel", null)

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun SearchCourseDeliverables(courseID: Int) {
        mDeliverablesViewModel.getAllDeliverablesOfACourse(courseID).observe(viewLifecycleOwner) { deliverables ->
            updateCourseGrade(deliverables)
            val adapter = SingleCourseAdapter(requireContext(), deliverables, mDeliverablesViewModel, this)
            mListView.adapter = adapter

            val courseCurrentGrade = binding.root.findViewById<TextView>(R.id.currentGradePercentage)
            val courseRunningGrade = binding.root.findViewById<TextView>(R.id.runningGradePercentage)
            val coursePercentComplete = binding.root.findViewById<TextView>(R.id.percentCompletePercentage)


            if(thisCourse.currentGrade != -1 && thisCourse.runningGrade != -1 && thisCourse.percentComplete != -1){
                courseCurrentGrade.text = thisCourse.currentGrade.toString() + "%"
                courseRunningGrade.text = thisCourse.runningGrade.toString() + "%"
                coursePercentComplete.text = thisCourse.percentComplete.toString() + "%"
            }else{
                courseCurrentGrade.text = "0%"
                courseRunningGrade.text = "0%"
                coursePercentComplete.text = "0%"
            }
        }
    }

    fun updateDeliverables(){
        //Temporary Placed here to update past dates
        val calendar = Calendar.getInstance()
        val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.time)

        mDeliverablesViewModel.updatePastDates(currentDateTime)
        mDeliverablesViewModel.updateFutureDates(currentDateTime)
    }

    fun updateCourseGrade(deliverables : List<Deliverable>){

        thisCourse.percentComplete = GradeCalculations.calculatePercentComplete(deliverables).toInt()
        thisCourse.runningGrade = GradeCalculations.calculateRunningGrade(deliverables).toInt()
        thisCourse.currentGrade = GradeCalculations.calculateCurrentGrade(deliverables).toInt()
        thisCourse.letterGrade = GradeCalculations.gradePointsToLetterGrade(thisCourse.currentGrade)

        mCourseViewModel.update(thisCourse)
    }

    fun refresh(root: View){

        updateDeliverables()

        val courseCurrentGrade = root.findViewById<TextView>(R.id.currentGradePercentage)
        val courseRunningGrade = root.findViewById<TextView>(R.id.runningGradePercentage)
        val coursePercentComplete = root.findViewById<TextView>(R.id.percentCompletePercentage)

        if(thisCourse.currentGrade != -1 && thisCourse.runningGrade != -1 && thisCourse.percentComplete != -1){
            courseCurrentGrade.text = thisCourse.currentGrade.toString() + "%"
            courseRunningGrade.text = thisCourse.runningGrade.toString() + "%"
            coursePercentComplete.text = thisCourse.percentComplete.toString() + "%"
        }else{
            courseCurrentGrade.text = "0%"
            courseRunningGrade.text = "0%"
            coursePercentComplete.text = "0%"
        }



    }

}