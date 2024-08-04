package ca.unb.mobiledev.group18project

import ca.unb.mobiledev.group18project.entities.Course
import ca.unb.mobiledev.group18project.entities.Deliverable

class GradeCalculations {

    //Percent complete: add all weight percentages for deliverables with grades entered(not null grades)
    //
    //Running grade: percent weight * grade entered  + running grade so far
    //
    //Current grade: running grade/ percent complete
    companion object Functions{
            fun calculatePercentComplete(deliverables: List<Deliverable>): Double {
                if(deliverables.isEmpty()){
                    return -1.0
                }
                return deliverables.filter { it.grade != null }
                    .sumOf { it.weight!!.toDouble() }
            }

            fun calculateRunningGrade(deliverables: List<Deliverable>): Double {
                if(deliverables.isEmpty()){
                    return -1.0
                }
                return deliverables.filter { it.grade != null }
                    .sumOf { (it.weight!!.toDouble() /100) * it.grade!! }
            }

            fun calculateCurrentGrade(deliverables: List<Deliverable>): Double {
                if(deliverables.isEmpty()){
                    return -1.0
                }
                val percentComplete = calculatePercentComplete(deliverables)
                return if (percentComplete > 0) {
                    (calculateRunningGrade(deliverables) * 100) / percentComplete
                } else {
                    0.0  // or handle this case as needed
                }
            }

        fun gradePointsToLetterGrade(percentage: Int): String {
            var gradePoint = percentageToGradePoints(percentage)
            return when {
                gradePoint == 4.3 -> "A+"
                gradePoint == 4.0 -> "A"  // A
                gradePoint == 3.7 -> "A-"  // A-
                gradePoint == 3.3 -> "B+"  // B+
                gradePoint == 3.0 -> "B"  // B
                gradePoint == 2.7 -> "B-"  // B-
                gradePoint == 2.3 -> "C+"  // C+
                gradePoint == 2.0 -> "C"  // C
                gradePoint == 1.0 -> "D"   // D
                else -> "F"              // F
            }
        }

            fun percentageToGradePoints(percentage: Int): Double {
                return when {
                    percentage >= 90 -> 4.3  // A+
                    percentage >= 85 -> 4.0  // A
                    percentage >= 80 -> 3.7  // A-
                    percentage >= 75 -> 3.3  // B+
                    percentage >= 70 -> 3.0  // B
                    percentage >= 65 -> 2.7  // B-
                    percentage >= 60 -> 2.3  // C+
                    percentage >= 55 -> 2.0  // C
                    percentage >= 50 -> 1.0   // D
                    else -> 0.0              // F
                }
            }

            fun calculateCourseGradePoints(course: Course): Double {
                val gradePoints = percentageToGradePoints(course.currentGrade)
                return course.ch * gradePoints
            }

            fun calculateCumulativeGPA(courses: List<Course>): Double {
                var course = courses.filter { it.letterGrade != null }
                val totalGradePoints = course.sumOf { calculateCourseGradePoints(it) }
                val totalCreditHours = course.sumOf { it.ch }
                return if (totalCreditHours > 0) totalGradePoints / totalCreditHours else 0.0
            }
    }
}