package ca.unb.mobiledev.group18project

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import ca.unb.mobiledev.group18project.ui.settings.SettingsFragment


class NotificationReceiver : BroadcastReceiver()  {

    override fun onReceive(context: Context, intent: Intent) {

        val deliverableId = intent.getIntExtra("deliverable_id", -1)

        val deliverableName = intent.getStringExtra("deliverable_name")

        val deliverableCourseName = intent.getStringExtra("deliverable_courseName")

        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("open_deliverable_fragment", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        SettingsFragment.selectedAlertValue

        var timeDue = SettingsFragment.alertChoices.filterValues { it == SettingsFragment.selectedAlertValue }.keys.firstOrNull() ?: "1 hour before"

        timeDue = timeDue.replace(" before", "")

        val notificationBuilder = NotificationCompat.Builder(context, "GradeTracker_CHANNEL_1")
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your own drawable resource
            .setContentTitle("$deliverableName Due Soon")
            .setContentText("$deliverableName in $deliverableCourseName is due in $timeDue.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(deliverableId, notificationBuilder.build())
    }

}
