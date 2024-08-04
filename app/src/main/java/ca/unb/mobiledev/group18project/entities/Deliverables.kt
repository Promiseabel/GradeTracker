package ca.unb.mobiledev.group18project.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "deliverables_table",
    foreignKeys = [ForeignKey(
    entity = Courses::class,
    childColumns = ["courseID"],
    parentColumns = ["courseID"])])
class Deliverables {
    @PrimaryKey(autoGenerate = true)
    var delivID : Int = 0
    var courseID : Int = 0
    var name: String = ""
}