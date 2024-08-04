package ca.unb.mobiledev.group18project.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.Date

@Entity(tableName = "deliverables_table",
    foreignKeys = [ForeignKey(
    entity = Course::class,
    childColumns = ["courseID"],
    parentColumns = ["courseID"], onDelete = ForeignKey.CASCADE)])
class Deliverable {
    @PrimaryKey(autoGenerate = true)
    var delivID : Int = 0

    var courseID : Int = 0

    var name: String? = ""

    var courseName: String? = ""

    var dueDate: String? = null  // Stored as "YYYY-MM-DD"

    var dueTime: String? = null  // Stored as "HH:MM"

    var completed: Boolean = false

    var weight: Int? = null

    var info: String = ""

    var grade: Int?= null

}