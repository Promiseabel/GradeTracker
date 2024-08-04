package ca.unb.mobiledev.group18project.model

import java.time.Period
import java.util.Date

data class Course(private val id: String?,
                  val name: String?,
                  val description: String? = null,
                  val startDate: Date?,
                  val endDate: Date?,
                  val gradingScheme: GradingScheme?){

}