**C195SoftwareII.Java**

Purpose of application:
The application utilizes a GUI to give users the ability to quickly and easily display and manipulate data within a MySQL 
database without any database knowledge.  Its main functionalities are: 
- Calendar view for appointments which track dates, times, customers, and employer contacts assigned to the appointments
- Customer maintenance for keeping track of customer's addresses and contact information
- The ability to add, modify, and delete appointments on the calendar
- Reports for data regarding appointments


Author:
Timothy Lawrence
tlawr33@wgu.edu  --  (801)643-5712
Version number: v1.02
August 2021


IDE: Apache NetBeans 12.1
JDK 15.0.1
JavaFX 11.0.1



----- **  How to run:  ** -----
Extract files from .zip file into NetBeans IDE.
Right click 'C195SoftwareII.java' in the c195softwareii package/folder and click 'Run File'
Application should compile and open, directing the user to the log in screen.
Log in using credentials to be directed to the main calendar screen.

The calendar screen will initially show appointments coming up within the week.
	Click 'Monthly view' at the top right to see all appointments in the current month.
	Click 'Weekly view' next to that to return to the initial weekly view.
Appointment buttons are at the bottom left of the calendar
	'Create Appt' will allow you to create a new appointment
	'Modify Appt' will take the currently selected appointment, whether on weekly or monthly view, and allow changes to the appointment
	'Delete Appt' will delete the currently selected appointment, after prompting for confirmation.
'Customer Maintenance' at the top left will direct to the customer maintenance screen.
	Customers are displayed on the right.
	Selecting a customer in the table will display all of their current contact information on the left
		Updating the information on the left and clicking 'Update' will update the customer's information
		Clicking 'Delete' wiill delete the currently selected customer, after prompting for confirmation.
	Clicking 'Create a new customer' at the top left will generate a new customer ID and set all fields to blank.
		Fill out the fields and click 'Create' to create a new customer with the specified contact information.
	Clicking 'Back to Calendar' will return to the main calendar screen.
'Reports' near the bottom right will direct to the Reports menu
	Clicking 'Total Appointments' will display the total number of appointments by month and type.
	Clicking 'Contact Schedules' will display one dialogue box for each contact with their upcoming schedules.
	Clicking 'Count Past Appointments' will give the number of past appointments, and list their appointment IDs.
	Clicking 'Back to Calendar' will return to the main calendar screen.
'Exit' at the bottom right will exit the program.



Third report on reports screen:
The third report chosen was created to show past appointments - It shows all appointments that have already transpired, 
which can be used to give a quick glance into how many appointments have been completed since beginning utilization 
of the program, and evaluating how effective it has been in keeping track of appointments for the company.  It can 
also be used to determine how many appointments are still 'clogging up' the database as they are no longer needed, 
should the company decide they do not need to keep an accurate history of appointments on file.


MySQL Connector driver version 8.0.25


v1.01 changes
Updated customer page for additional clarity in adding new customers - "Create new customer" button must be clicked first to
populate the new customer ID field, and the final 'Create' button will not be visible until clicking the first button.

v1.02 changes
Application now always notifies on login whether there is an appointment in the next 15 minutes or not
Added combobox on add and modify appointment pages for User input
Added ability to modify or delete appointments from the monthly view
Added full information for how to run and use program to the README file