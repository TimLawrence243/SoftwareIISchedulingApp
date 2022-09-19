/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.Time;
import utils.DBConnection;


/**
 * Controller class for Reports Menu.
 * Contains the methods utilized when generating each of the three reports.
 * 
 * @author tim83
 */
public class reportsMenuController implements Initializable {
    Stage stage;
    Parent scene;
    
    /**
     * Generates the schedule for each contact in the database.
     * Includes Appointment ID, title, type, description, start date/time, end date/time, and the customer ID for the appointment.
     * Opens an 'alert' box with the schedule of each contact
     * @param event Button clicked
     */
    @FXML
    void onActionContSchedules(ActionEvent event) {
        //ArrayList of all contactIDs
        ArrayList<Integer> contIDs = new ArrayList<Integer>();
        
        Statement stmt;
        ResultSet rs = null;
        
        //Get all contact IDs in the database
        try{
            stmt = DBConnection.conn.createStatement();

            rs = stmt.executeQuery("SELECT * FROM contacts");

            while (rs.next()){
                contIDs.add(rs.getInt("Contact_ID"));
            }
                
        } catch (SQLException e){
            System.out.println("Error getting contacts list for contact IDs");
        }
        
        //Get appointments for each contact ID
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        
        for(Integer id : contIDs){
            String scheduleString = "Contact ID: " + id + " \n";
            try{
            stmt = DBConnection.conn.createStatement();

            rs = stmt.executeQuery("SELECT * FROM appointments WHERE Contact_ID = " + id);

            while (rs.next()){
                //Check if the start is after 'now' - Not listing past appointments for schedule display
                
                //Get start timestamp, convert to local time, store that local time
                //Getting start TS first since we need to convert it to local time for comparison to 'now'
                Timestamp startTS = rs.getTimestamp("Start");
                Timestamp start = Time.utcToZone(startTS);
                if(start.after(now)){
                    int apptid = rs.getInt("Appointment_ID");
                    String title = rs.getString("Title");
                    String description = rs.getString("Description");
                    String type = rs.getString("Type");
                    
                    //Get end timestamp, convert to local time, store that local time
                    Timestamp endTS = rs.getTimestamp("End");
                    Timestamp end = Time.utcToZone(endTS);
                    int custid = rs.getInt("Customer_ID");
                    
                    scheduleString = scheduleString.concat("ID: " + apptid + "\n "
                            + title + " | " + description + " | " + type + " Customer: " + custid + " \n "
                            + start + " -- " + end + "\n \n");
                }
            }
            
            //Show an alert box for each contact and their schedule
            Alert alert = new Alert(Alert.AlertType.INFORMATION, scheduleString);
            alert.showAndWait();
            
                
        } catch (SQLException e){
            System.out.println("Error getting contacts list for contact IDs");
        }
        }
    }

    /**
     * Returns to the calendar page from reports page
     * @param event Button clicked
     * @throws IOException 
     */
    @FXML
    void onActionExit(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/calMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Generates a report of total appointments in database for each month and type.
     * Opens an 'alert' box that displays the information
     * @param event Button clicked
     */
    @FXML
    void onActionTotalAppts(ActionEvent event) {
        Statement stmt;
        ResultSet rs = null;
        
        int janCount = 0;
        int febCount = 0;
        int marCount = 0;
        int aprCount = 0;
        int mayCount = 0;
        int junCount = 0;
        int julCount = 0;
        int augCount = 0;
        int sepCount = 0;
        int octCount = 0;
        int novCount = 0;
        int decCount = 0;
        
        ArrayList<String> typeArray = new ArrayList<String>();
        Timestamp ts;
        
        
        
        try{
            stmt = DBConnection.conn.createStatement();

            rs = stmt.executeQuery("SELECT * FROM appointments");

            while (rs.next()){
                typeArray.add(rs.getString("Type"));
                
                ts = rs.getTimestamp("Start");
                switch(ts.getMonth()){
                    case 0:
                        janCount++;
                        break;
                    case 1:
                        febCount++;
                        break;
                    case 2:
                        marCount++;
                        break;
                    case 3:
                        aprCount++;
                        break;
                    case 4:
                        mayCount++;
                        break;
                    case 5:
                        junCount++;
                        break;
                    case 6:
                        julCount++;
                        break;
                    case 7:
                        augCount++;
                        break;
                    case 8:
                        sepCount++;
                        break;
                    case 9:
                        octCount++;
                        break;
                    case 10:
                        novCount++;
                        break;
                    case 11:
                        decCount++;
                        break;
                }
                
            }
        } catch (SQLException e){
            System.out.println("Error getting appointments");
        }
        //Map for storing each type and number of occurences of that type
        Map<String, Integer> repeatMap = new HashMap<String, Integer>();
        
        for(String s : typeArray){
            //If the Map already contains the key (type), +1 to the value (count)
            if(repeatMap.containsKey(s)){
                repeatMap.put(s, repeatMap.get(s) + 1);
            } else {
                //Else, put the new type into the map with a count of 1
                repeatMap.put(s, 1);
            }
        }
        
        //Create string of all types and number of appointments of that type
        String typeString = "";
        
        for(Map.Entry<String, Integer> entry : repeatMap.entrySet()){
            typeString = typeString.concat(entry.getKey() + ":   " + entry.getValue() + " \n");
//            System.out.println("Type = " + entry.getKey() +
//                               ", repeats = " + entry.getValue());
        }
        
        //Display an alert box with the report information
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Total appointments by month: "
                + "\n January: " + janCount + "\n February: " + febCount + "\n March: " + marCount
                + "\n April: " + aprCount + "\n May: " + mayCount + "\n June: " + junCount
                + "\n July: " + julCount + "\n August: " + augCount + "\n September: " + sepCount
                + "\n October: " + octCount + "\n November: " + novCount + "\n December: " + decCount + "\n \n"
                + "Total appointments by type: \n"
                + typeString);
        alert.showAndWait();
        
    }
   
    /**
     * Third report: Count past appointments
     * Provides a count of appointments that have passed, displays the total number 
     * and appointment IDs of each appointment that has passed.
     * LAMBDA -- Used to output each past appointment ID
     * @param event Button clicked
     */
    @FXML
    void onActionThirdReport(ActionEvent event) {
        Statement stmt;
        ResultSet rs = null;
        
        int count = 0;
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        ArrayList<Integer> IDs = new ArrayList<Integer>();
        
        try{
            stmt = DBConnection.conn.createStatement();

            rs = stmt.executeQuery("SELECT * FROM appointments");

            while (rs.next()){
                Integer apptID = rs.getInt("Appointment_ID");
                Timestamp endTS = rs.getTimestamp("End");
                Timestamp end = Time.utcToZone(endTS);
                
                //if the appointment ended before 'now', iterate the counter and add the appointment's ID to arraylist
                if(end.before(now)){
                    count++;
                    IDs.add(apptID);
                }
            }
            
            //Set up StringBuilder to append each ID and a new line, starting with "IDs of past appointments:"
            StringBuilder pastIDs = new StringBuilder();
            String pastIDsStart = "IDs of past appointments: \n";
            pastIDs.append(pastIDsStart);
            
            //LAMBDA
            //For each ID, concatenate to string pastIDs and format with a new line
            IDs.forEach( (i) -> { pastIDs.append(i.toString() + "\n"); } );
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Detected past appointments: " + count + "\n \n" + pastIDs);
            alert.showAndWait();
            
        } catch (SQLException e){
            System.out.println("Error getting appointments");
        }
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    
    
}
