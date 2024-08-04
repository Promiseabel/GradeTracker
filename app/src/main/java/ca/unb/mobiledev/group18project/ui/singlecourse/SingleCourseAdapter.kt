package ca.unb.mobiledev.group18project.ui.singlecourse

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import ca.unb.mobiledev.group18project.NotificationReceiver
import ca.unb.mobiledev.group18project.R
import ca.unb.mobiledev.group18project.entities.Course
import ca.unb.mobiledev.group18project.entities.Deliverable
import ca.unb.mobiledev.group18project.ui.courses.CoursesFragment
import ca.unb.mobiledev.group18project.ui.courses.CoursesViewModel
import ca.unb.mobiledev.group18project.ui.deliverables.DeliverableAdapter
import ca.unb.mobiledev.group18project.ui.deliverables.DeliverablesViewModel
import ca.unb.mobiledev.group18project.ui.settings.SettingsFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SingleCourseAdapter(context: Context, items: List<Deliverable>, private val viewmodel: DeliverablesViewModel, private val fragment: SingleCourseFragment) : ArrayAdapter<Deliverable>(
    context, 0, items) {

    private lateinit var actionBar : ActionBar

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)

        var currView = convertView
        if (currView == null) {
            currView = LayoutInflater.from(context).inflate(R.layout.coursedeliverablelist_layout, parent, false)
        }

        val deliverableName = currView!!.findViewById<TextView>(R.id.deliverable_name)
        val deliverableDate = currView!!.findViewById<TextView>(R.id.deliverable_date)
        val deliverableWeight = currView!!.findViewById<TextView>(R.id.deliverable_weight)
        val deliverableGrade = currView!!.findViewById<TextView>(R.id.deliverable_grade)

        val deliverableMenu = currView!!.findViewById<ImageView>(R.id.image_menu)
        if(item!!.grade != null) {
            deliverableGrade.text = "Grade: "+item!!.grade.toString()+"%"
        }else{
            deliverableGrade.text = "No Grade"
        }

        deliverableName.text = item!!.name
        deliverableDate.text = formatDate(item.dueDate) +" "+ formatTime(item.dueTime)
        deliverableWeight.text = "Weight: "+item.weight.toString() + "%"

        deliverableMenu.setOnClickListener {
            val popup = PopupMenu(context, deliverableMenu)
            popup.inflate(R.menu.courselist_menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_option_edit -> {
                        fragment.BuildDialog("EDIT DELIVERABLE", item, false)
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

        scheduleNotification(item)

        return currView!!
    }

    fun formatDate(dueDate: String?): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        if(dueDate.isNullOrEmpty()){
            return ""
        }
        val date = inputDateFormat.parse(dueDate)
        val formattedDate = outputDateFormat.format(date)
        return "$formattedDate"
    }

    fun formatTime(dueTime: String?) : String{
        val inputTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputTimeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        if(dueTime.isNullOrEmpty()){
            return ""
        }
        val time = inputTimeFormat.parse(dueTime)
        val formattedTime = outputTimeFormat.format(time)
        return "$formattedTime"

    }

    fun scheduleNotification(deliverable: Deliverable) {


        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("deliverable_id", deliverable.delivID)
        intent.putExtra("deliverable_name", deliverable.name)
        intent.putExtra("deliverable_courseName", deliverable.courseName)
        val pendingIntent = PendingIntent.getBroadcast(context, deliverable.delivID, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationTime = getNotificationTime(deliverable.dueDate, deliverable.dueTime)

        if(notificationTime.timeInMillis < System.currentTimeMillis()){
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() == true) {
                alarmManager?.setExact(AlarmManager.RTC_WAKEUP, notificationTime.timeInMillis, pendingIntent)
            } else {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
            }
        } else {
            alarmManager?.setExact(AlarmManager.RTC_WAKEUP, notificationTime.timeInMillis, pendingIntent)
        }

    }

    fun getNotificationTime(dueDate: String?, dueTime: String?): Calendar {
        val cal = Calendar.getInstance()
        if(dueTime.isNullOrEmpty()){
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            cal.time = sdf.parse("$dueDate")!!
        }else{
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            cal.time = sdf.parse("$dueDate $dueTime")!!
        }

        if(SettingsFragment.selectedAlertValue == 0){
            cal.add(Calendar.MINUTE, -1)
        }else{
            cal.add(Calendar.HOUR, SettingsFragment.selectedAlertValue)
        }

        return cal
    }


}