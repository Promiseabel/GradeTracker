package ca.unb.mobiledev.group18project.ui.courses

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import ca.unb.mobiledev.group18project.GradeCalculations
import ca.unb.mobiledev.group18project.R
import ca.unb.mobiledev.group18project.entities.Course
import ca.unb.mobiledev.group18project.ui.singlecourse.SingleCourseFragment
import java.text.SimpleDateFormat
import java.util.Locale


class CoursesAdapter(context: Context, items: List<Course>, private val viewmodel: CoursesViewModel, private val fragment: CoursesFragment) : ArrayAdapter<Course>(
    context, 0, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the data item for this position
        val item = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        var currView = convertView
        if (currView == null) {
            currView = LayoutInflater.from(context).inflate(R.layout.courselist_layout, parent, false)
        }

        // Lookup view for data population
        val courseName = currView!!.findViewById<TextView>(R.id.course_name)
        val courseCH = currView!!.findViewById<TextView>(R.id.course_ch)
        val courseMenu = currView!!.findViewById<ImageView>(R.id.image_menu)
        val courseLetterGrade = currView!!.findViewById<TextView>(R.id.course_letterGrade)
        val courseDate = currView!!.findViewById<TextView>(R.id.course_date)

        if(item!!.currentGrade == -1 && item.runningGrade == -1 && item.percentComplete == -1){
            courseLetterGrade.visibility = View.INVISIBLE
        }else{
            courseLetterGrade.text = item!!.letterGrade
            courseLetterGrade.visibility = View.VISIBLE
        }

        courseName.text = item!!.name
        courseCH.text = "${item.ch}ch"
        courseDate.text = formatDate(item.startDate) + " - " + formatDate(item.endDate)

        currView.setOnClickListener {
            // Navigate to the SingleCourseFragment
            val singleCourseFragment = SingleCourseFragment()

            // Pass the clicked course to the fragment using a Bundle
            val bundle = Bundle()
            bundle.putSerializable("course", item)
            singleCourseFragment.arguments = bundle

            // Use findNavController to navigate to SingleCourseFragment with the bundle

            if (fragment is CoursesHistoryFragment) {
                fragment.findNavController().navigate(R.id.action_navigation_courses_history_to_navigation_single_course, bundle)
            }else if (fragment is CoursesFragment){
                fragment.findNavController().navigate(R.id.action_navigation_courses_to_navigation_single_course, bundle)
            }

        }

        courseMenu.setOnClickListener {
            val popup = PopupMenu(context, courseMenu)
            popup.inflate(R.menu.courselist_menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_option_edit -> {
                        fragment.BuildDialog("EDIT COURSE", item, false)
                        true
                    }
                    R.id.menu_option_delete -> {
                        viewmodel.delete(item)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }



        // Return the completed view to render on screen
        return currView
    }

    fun formatDate(date: String?): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        if(date.isNullOrEmpty()){
            return ""
        }
        val date = inputDateFormat.parse(date)
        val formattedDate = outputDateFormat.format(date)
        return "$formattedDate"
    }

}