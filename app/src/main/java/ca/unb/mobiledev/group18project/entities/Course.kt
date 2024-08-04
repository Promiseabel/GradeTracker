package ca.unb.mobiledev.group18project.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "courses_table")
class Course : Serializable {
    @PrimaryKey(autoGenerate = true)
    var courseID = 0

    var name: String? = null

    var ch : Int = 0

    var startDate: String? = null  // Stored as "YYYY-MM-DD"

    var endDate: String? = null  // Stored as "YYYY-MM-DD"

    var completed: Boolean = false

    var info: String = ""

    var letterGrade: String? = null

    var percentComplete: Int = -1

    var currentGrade: Int = -1

    var runningGrade: Int = -1

}