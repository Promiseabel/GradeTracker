Grade Tracker
By: Promise Abel, Justin Gallant and Kristyn Serniuk


Motivation

An application made by students for students. We understand the stress of grades and decided to implement an application to help ease that stress by conveniently tracking your grade and its influencers; i.e. assignments, tests, etc. GradeTracker continues where other educational applications stop, rather than just knowing your score for a single deliverable, it can take it further by looking at how that current score has affected your overall grade.

GradeTracker is a comprehensive mobile application designed to simplify the process of tracking and calculating grades for students no matter the grade level. Whether in high school, university or even an educator, GradeTracker is here to make managing your grades easy. The application’s core functionality is to provide a user-friendly platform for entering, calculating, and analyzing your grades, helping users stay on top of their academic performance.

Features

Grade Tracking: Easily input what you have due, how much it is worth and the grade you received, and the app will calculate your current grade based on the weighted categories.   
Due date reminders: automatically organize due dates in order of date and get reminders when things are coming up.   
GPA Calculator: Calculate your cumulative GPA by adding multiple courses, making it easy to monitor your academic progress over time. Simply input your courses and their credit hours, and the app will compute your precise cumulative GPA, making academic planning a breeze.  
Grade History: Maintain a history of past courses for future reference.

Out of Scope- Grade Predictions: Get insights into what grades you need in order to achieve your desired final grade in a course. You can enter your target grade and the app will calculate what you need to get on upcoming deliverables to obtain your goal.

Release Notes and Known Issues

During the design process we decided to lower the priority of the exam grade predictor that was in our initial design. We also decided, due to time constraints as well as the instructor feedback to not to implement the grading scales for the gpa calculator that we discussed during the prototype demonstration and only develop one standard scale. As we wanted to prioritize the other features Overall, we haven't made any other changes to the scope from the original project and the project goal was met.



Complete features:
Courses page:
-	Users may enter courses with the appropriate course information (course name, credit hours, start/end dates, and general info) if not all required fields are filled out a toast is shown to indicate the course couldn’t be added.
-	Edit existing courses. Toast to display if updated.
-	Delete existing courses. Toast to display course was deleted.
-	When clicking on a specific course, users can see a list of deliverables assigned under that course.
-	They can also add, edit, and delete deliverables specifically for that selected course on the selected courses page.

Grade Calculations:
-	Grade calculations are completed at the the bottom of the courses page. When a user add or edits grades received three calculations are then performed and displayed:
     percent complete, current grade and running grade.

Deliverables page:
-	Users may enter deliverables with the appropriate deliverable information (deliverable name, course name, percent worth, due date/time, grade received and general info) if not all required fields are filled out a toast is shown to indicate the deliverable couldn’t be added.
-	Edit existing deliverable (change the course, name, due date, worth or grade at any time) Toast to display if updated or if user makes an error it will not update.
-	Delete existing deliverable. Toast to display deliverable was deleted.
-	When a user selects a deliverable the course page will open to show for the specific deliverables course.

Deliverable Notifications:
-	When a deliverable is coming up to the due date a notification will be displayed on the users device.
-	Under settings users can change when they receive notifications.

Course history:
-	When a class has come to the end date the class automatically moves to course history which then users can view all past classes and grades under the course history page.

GPA calculator:
-	When a user enters grades for a course the overall course grade and credit hours is then used to calculate the students GPA which is then displayed so students can know what their GPA is and how each deliverable and course impacts it.
     Known issues:

We know our app is not 100% flushed out due to time constraints of the class so a few known issues we have are overall design and formatting can be revised to provide a more appealing app for users. Throughout the planning process we also moved features into lower priority which included the grade predictions. This feature in our app was out of scope for this class but this would be something we would have liked to implement if we were to continue it into the winter.

Some other issues we know we would eventually like to fix:
-	We wanted to set constraints so that course end date must be after course start date.
-	Having a warning if users enter more then 100% for the weight of deliverables in a course.
-	Currently courses can be set to course history even if all deliverables are not complete.

Supported API levels
-	We did most of our developing and testing on API level 30

Test cases / test plans
No specific scenario is needed just testing of all features:
-	Adding a course
-	Adding deliverables for that course
-	Edit or delete courses
-	Edit or delete deliverables
-	Adding a course that’s in the past to show course history
-	Setting notifications to 1 min before and set due date so that notifications are displayed.


