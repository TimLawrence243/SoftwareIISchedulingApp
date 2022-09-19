/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import static java.lang.Integer.parseInt;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DBConnection;
import java.util.GregorianCalendar;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;

/**
 * Class for Appointments which is used to store all the appointments in the database 
 * in our own format for easier use within the application.
 * @author tim83
 */
public class Appointment{
    
    private int apptID;
    private String title;
    private String description;
    private String location;
    private String type;
    private Timestamp start;
    private Timestamp end;

    public int getApptID() {
        return apptID;
    }

    public void setApptID(int apptID) {
        this.apptID = apptID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public int getCustID() {
        return custID;
    }

    public void setCustID(int custID) {
        this.custID = custID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getContID() {
        return contID;
    }

    public void setContID(int contID) {
        this.contID = contID;
    }
    
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    private int custID;
    private int userID;
    private int contID;
    private int dayOfWeek;
    
    
    
    public Appointment(int apptID, String title, String description, String location, String type, Timestamp start, 
            Timestamp end, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy, int custID,
            int userID, int contID, int dayOfWeek){
        this.apptID = apptID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.custID = custID;
        this.userID = userID;
        this.contID = contID;
        this.dayOfWeek = dayOfWeek;
    }

    
    
    private static ObservableList<Appointment> allAppts = FXCollections.observableArrayList();
    private static ObservableList<Appointment> weeklyAppts = FXCollections.observableArrayList();
    private static ObservableList<Appointment> monthlyAppts = FXCollections.observableArrayList();
    
    public static String currentUser;
    public static int currentUserID;
    private static int highestAppt;
    public static Boolean initialLogin = true;
    
    
    /**
     * Reach to Database to get all appointments and store them into allAppts.
     * Then, sort allAppts list by start date/time, and organize into two separate
     * lists, weeklyAppts and monthlyAppts, which are used to populate the weekly 
     * and monthly table views on main Calendar page.
     * Method is called in Initialize method of calMenuController
     */
    public static void generateAllAppts() {
        //Blocks used to distinguish days on weekly view
        Appointment monBlock = new Appointment(0, "Monday", "-----", "-----", "-----", null, null, null, null, null, null, 0, 0, 0, 0);
        Appointment tueBlock = new Appointment(0, "Tuesday", "-----", "-----", "-----", null, null, null, null, null, null, 0, 0, 0, 0);
        Appointment wedBlock = new Appointment(0, "Wednesday", "-----", "-----", "-----", null, null, null, null, null, null, 0, 0, 0, 0);
        Appointment thuBlock = new Appointment(0, "Thursday", "-----", "-----", "-----", null, null, null, null, null, null, 0, 0, 0, 0);
        Appointment friBlock = new Appointment(0, "Friday", "-----", "-----", "-----", null, null, null, null, null, null, 0, 0, 0, 0);
        Appointment satBlock = new Appointment(0, "Saturday", "-----", "-----", "-----", null, null, null, null, null, null, 0, 0, 0, 0);
        Appointment sunBlock = new Appointment(0, "Sunday", "-----", "-----", "-----", null, null, null, null, null, null, 0, 0, 0, 0);
        
        Statement stmt;
        ResultSet rs = null;
         
        
        //Clear allAppts first so we don't get duplicate data
        weeklyAppts.clear();
        monthlyAppts.clear();
        allAppts.clear();
        
        try{
            stmt = DBConnection.conn.createStatement();
            
            rs = stmt.executeQuery("SELECT * FROM appointments");
            highestAppt = 0;
            
            while (rs.next()){
                //Reset appointment a after each iteration
                Appointment a = new Appointment(0, null, null, null, null, null, null, null, null, null, null, 0, 0, 0, 0);
                //Add all columns from database into a
                a.apptID = rs.getInt("Appointment_ID");
                //Get highest apptID for creating new appointments
                if(a.apptID > highestAppt){
                    highestAppt = a.apptID;
                }
                
                a.title = rs.getString("Title");
                a.description = rs.getString("Description");
                a.location = rs.getString("Location");
                a.type = rs.getString("Type");
                //Get start timestamp, convert to local time, store that local time in ObservableList
                Timestamp startTS = rs.getTimestamp("Start");
                a.start = Time.utcToZone(startTS);
                //Get start timestamp, convert to local time, store that local time in ObservableList
                Timestamp endTS = rs.getTimestamp("End");
                a.end = Time.utcToZone(endTS);
                //Get create timestamp, convert to local time, store that local time in ObservableList
                Timestamp createTS = rs.getTimestamp("Create_Date");
                a.createDate = Time.utcToZone(createTS);
                
                a.createdBy = rs.getString("Created_By");
                //Get update timestamp, convert to local time, store that local time in ObservableList
                Timestamp updateTS = rs.getTimestamp("Last_Update");
                a.lastUpdate = Time.utcToZone(updateTS);
                
                a.lastUpdatedBy = rs.getString("Last_Updated_By");
                a.custID = parseInt(rs.getString("Customer_ID"));
                a.userID = parseInt(rs.getString("User_ID"));
                a.contID = parseInt(rs.getString("Contact_ID"));
                /*Get day of week the start date is on
                1 is Sunday
                2 is Monday
                3 is Tuesday. . .
                7 is Saturday
                */
                Calendar cal = Calendar.getInstance();
                cal.setTime(a.start);
                a.dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
                //System.out.println(a.dayOfWeek);
                //Add to allAppts
                allAppts.add(a);
                
            }
            
        } catch (SQLException e){
            System.out.println("Error adding appointment to allAppts");
        }
        
        //Sort allAppts by Start Date, starting with the earliest appointment
        FXCollections.sort(allAppts, new ApptDateComparator());
        
        //Set size outside of loop (prevents any infinite loops)
        int allApptsSize = allAppts.size();
        
        //Timestamp set for 'now' for comparisons
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        Timestamp sevenDays = Time.sevenDays(now);
        
        //
        //WEEKLY APPOINTMENTS
        //Adds all appointments to weekly view
        //
        
        //Used to determine what day was last read
        int currentDay = 0;
            for(int i = 0; i < allApptsSize; i++){
                Appointment a = allAppts.get(i);
                //If the appointment is between 'now' and 'sevenDays' from now, add it to weekly appointments plus formatting
                if(a.start.after(now) && a.start.before(sevenDays)){
                
                    //If the next appointment is not the same day of the week, it's not the same day, so we need a new Block
                    if(a.dayOfWeek != currentDay){
                        currentDay = a.dayOfWeek;
                        //Add 'Block' for day of week to the weeklyAppts list
                        switch(a.dayOfWeek){
                            case 1:
                                weeklyAppts.add(sunBlock);
                                break;
                            case 2:
                                weeklyAppts.add(monBlock);
                                break;
                            case 3:
                                weeklyAppts.add(tueBlock);
                                break;
                            case 4:
                                weeklyAppts.add(wedBlock);
                                break;
                            case 5:
                                weeklyAppts.add(thuBlock);
                                break;
                            case 6:
                                weeklyAppts.add(friBlock);
                                break;
                            case 7:
                                weeklyAppts.add(satBlock);
                                break;
                        }
                    }
                    //Add appointment to weekly list.  If day was the same as last read, don't add a new 'Block', only appt
                    weeklyAppts.add(a);
                }
            }
            
            //
            //MONTHLY APPOINTMENTS
            //Adds all appointments for monthly view
            //
            
            for(int i = 0; i < allApptsSize; i++){
                Appointment a = allAppts.get(i);
                //If the appointment is in the same month as 'now', we will format and add it to monthlyAppts
                if(a.getStart().getMonth() == now.getMonth()){
                    monthlyAppts.add(a);
                }
            }
            
    }
    
    /**
     * Get stored list of ALL appointments, sorted by start date/time.
     * @return ObservableList of ALL appointments
     */
    public static ObservableList<Appointment> getAllAppts(){
        return allAppts;
    }
    
    /**
     * Get stored list of weekly appointments.  Organized to only contain appointments
     * between 'now' and 7 days from now.
     * @return ObservableList of all appointments for the next 7 days, from 'now'
     */
    public static ObservableList<Appointment> getWeeklyAppts(){
        return weeklyAppts;
    }
    
    /**
     * Get stored list of monthly appointments.  Organized to only contain appointments 
     * within the current month.
     * @return ObservableList of all appointments in the current month, both those that have passed
     * and appointments coming up.
     */
    public static ObservableList<Appointment> getMonthlyAppts(){
        return monthlyAppts;
    }
    
    /**
     * Returns highest appointment ID currently in the database.  Used for 
     * generating new appointment IDs when creating new appointments.
     * @return highestAppt ID currently stored.
     */
    public static int apptNumber(){
        return highestAppt;
    }
    
    /**
     * Checks if there is an appointment within 15 minutes of 'now'.
     * Used for checking and then generating an alert if there is an upcoming appointment in calMenuController initialization.
     */
    public static void checkForFifteen(){
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        Timestamp fifteen = Timestamp.valueOf(LocalDateTime.now().plusMinutes(15));
        Boolean apptFound = false;
        
        for(Appointment a : allAppts){
            //If the appointment starts after 'now' and before fifteen minutes from now, display alert.
            if(a.getStart().after(now) && a.getStart().before(fifteen)){
                apptFound = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment " + a.getApptID() + " for: " + a.getTitle() 
                        + " on " + (a.getStart().getMonth() + 1) + "/" + (a.getStart().getDate())
                        + " at " + (a.getStart().getHours() + 1) + ":" + (a.getStart().getMinutes() + 1)
                        + " " 
                        + " is coming up within the next 15 minutes.");
                alert.showAndWait();
            }  
        }
        
        if(apptFound == false){
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No appointments coming up in the next 15 minutes.");
                alert.showAndWait();
            }
    }
    
    
    
            
}

class ApptDateComparator implements Comparator<Appointment>{

    //Used to sort appointments in ascending (Soonest to latest) order
    @Override
    public int compare(Appointment a1, Appointment a2) {
         long t1 = a1.getStart().getTime();
        long t2 = a2.getStart().getTime();
        if(t2 < t1)
            return 1;
        else if(t1 > t2)
            return -1;
        else
            return 0;
    }
}
