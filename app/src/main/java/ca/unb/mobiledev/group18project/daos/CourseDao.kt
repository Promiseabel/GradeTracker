package ca.unb.mobiledev.group18project.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ca.unb.mobiledev.group18project.entities.Course

@Dao
interface CourseDao {

    @Query("SELECT * from courses_table ORDER BY courseID ASC")
    fun listAllCourses(): LiveData<List<Course>>

    @Query("SELECT * from courses_table WHERE completed = '0' ORDER BY name ASC ")
    fun listAllIncompleteCourses(): LiveData<List<Course>>

    @Query("SELECT * from courses_table WHERE completed = '1' ORDER BY endDate DESC")
    fun listAllCompletedCourses(): LiveData<List<Course>>

    //No need for this?
    //@Query("SELECT * from courses_table WHERE name = :name ORDER BY name ASC")
    //fun findCourseByName(name: String): List<Courses>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCourse(course: Course)

    //No need for this? also seems like you can have duplicate name so this function proves hazardous
    //@Query("DELETE FROM courses_table WHERE name = :courseName")
    //fun deleteCourseByName(courseName: String)

    @Delete
    fun deleteCourse(course: Course)

    @Update
    fun updateCourse(course: Course)

    @Query("UPDATE courses_table SET completed = '1' WHERE endDate < :currentDate")
    fun updatePastDates(currentDate: String)

    @Query("UPDATE courses_table SET completed = '0' WHERE endDate > :currentDate")
    fun updateFutureDates(currentDate: String)
}