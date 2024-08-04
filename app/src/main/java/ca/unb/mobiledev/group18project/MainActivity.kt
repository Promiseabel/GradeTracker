package ca.unb.mobiledev.group18project

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ca.unb.mobiledev.group18project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    object Constants {
        // Strings will serve as keys when saving state between activities
        const val NOTIFICATION_CHANNEL_ID = "GradeTracker_CHANNEL_1"
        const val NOTIFICATION_REQUEST_ID = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        val navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_courses,R.id.navigation_deliverables, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        checkNotificationPermissions()
        createNotificationChannel()

        if (intent.getBooleanExtra("open_deliverable_fragment", false)) {
            findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_deliverables)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
    }

    fun hideBottomNav() {
        binding.navView.visibility = View.GONE
    }

    fun showBottomNav() {
        binding.navView.visibility = View.VISIBLE
    }

    private fun checkNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestRuntimePermissions()
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Deliverable Notifications"
            val descriptionText = "Notifications for deliverables"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestRuntimePermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.POST_NOTIFICATIONS
        ), Constants.NOTIFICATION_REQUEST_ID)
    }



}