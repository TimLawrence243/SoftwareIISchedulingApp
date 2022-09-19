package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.Time;
import utils.DBConnection;

public class ModApptController implements Initializable {

    @FXML
    private TextField createApptIDTxt;

    @FXML
    private TextField createApptTitleTxt;

    @FXML
    private TextField createApptDescTxt;

    @FXML
    private TextField createApptLocTxt;
    
    @FXML
    private TextField createApptTypeTxt;

    @FXML
    private ComboBox<String> createApptContCmb;
    
    @FXML
    private ComboBox<String> createApptCustCmb;
    
    @FXML
    private ComboBox<String> createApptUserCmb;
    
    @FXML
    private DatePicker createApptDatePick;

    @FXML
    private ComboBox<String> createApptStartHour;

    @FXML
    private ComboBox<String> createApptStartMin;

    @FXML
    private ComboBox<String> createApptEndHour;

    @FXML
    private ComboBox<String> createApptEndMin;
    
    Stage stage;
    Parent scene;

    //Hashmap for storing which contact has which Contact_ID
    Map <String, Integer> contactPairs = new HashMap<String, Integer>();
    //Hashmap for storing which customer has which Customer_ID
    Map <String, Integer> custPairs = new HashMap<String, Integer>();
    //Hashmap for storing which user has which User_ID
    Map <String, Integer> userPairs = new HashMap<String, Integer>();
    //Hashmap for storing which Contact_ID identifies which contact
    Map <Integer, String> contactPairsRev = new HashMap<Integer, String>();
    //Hashmap for storing which Customer_ID identifies which customer
    Map <Integer, String> custPairsRev = new HashMap<Integer, String>();
    //Hashmap for storing which User_ID identifies which user
    Map <Integer, String> userPairsRev = new HashMap<Integer, String>();
    
    public static Appointment oldAppt;
    
    public void sendAppt(Appointment appt){
        oldAppt = appt;
        
//        createApptIDTxt.setText(String.valueOf(appt.getApptID()));
//        createApptTitleTxt.setText(appt.getTitle());
//        createApptDescTxt.setText(appt.getDescription());
//        createApptLocTxt.setText(appt.getLocation());
//        createApptTypeTxt.setText(appt.getType());
        
    }
    
    @FXML
    void onActionClose(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will clear all fields and return to calendar, continue?");
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if(result.isPresent() && result.get() == ButtonType.OK){
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/calMenu.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    @FXML
    void onActionCreate(ActionEvent event) {
        
        int ID = Integer.parseInt(createApptIDTxt.getText());
        String title = createApptTitleTxt.getText();
        String description = createApptDescTxt.getText();
        String location = createApptLocTxt.getText();
        String type = createApptTypeTxt.getText();
        
        //Store timezone difference
        int timeDiff = Time.timezoneDifferenceToEST();
        
        //Store boolean for appointment overlap (used below in determining if previous customer appointments overlap)
        Boolean apptOverlap = false;
                
        if(title.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment title can not be empty (All fields required)");
            alert.showAndWait();
        } else if (description.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment description can not be empty (All fields required)");
            alert.showAndWait();
        } else if (location.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment location can not be empty (All fields required)");
            alert.showAndWait();
        } else if (type.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment type can not be empty (All fields required)");
            alert.showAndWait();
        } else if (createApptContCmb == null || createApptCustCmb == null || createApptUserCmb == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment contact, customer, and user must be assigned, can not be empty (All fields required)");
            alert.showAndWait();
        } else if(createApptDatePick == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please select a date on the calendar (All fields required)");
            alert.showAndWait();
        } else if (createApptStartHour == null || createApptStartMin == null || createApptEndHour == null || createApptEndMin == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please select all start and end times, hours and minutes (All fields required)");
            alert.showAndWait();
        } else {
            //Retrieve ID of customer, contact, and user based on Name selected in combobox (Stored in pairs map)
            int contID = contactPairs.get(createApptContCmb.getValue());
            int custID = custPairs.get(createApptCustCmb.getValue());
            int userID = userPairs.get(createApptUserCmb.getValue());
            
            //Get individual values of year, month, and day selected on calendar
            int year = createApptDatePick.getValue().getYear() - 1900; //Subtract 1900 because timestamp year is year - 1900 
            int month = createApptDatePick.getValue().getMonthValue() - 1; //Subtract 1 because timestamp month is 0-11
            int day = createApptDatePick.getValue().getDayOfMonth();
            
            //Get start hours and minutes and end hours and minutes
            int startHour = Integer.parseInt(createApptStartHour.getValue());
            int startMin = Integer.parseInt(createApptStartMin.getValue());
            int endHour = Integer.parseInt(createApptEndHour.getValue());
            int endMin = Integer.parseInt(createApptEndMin.getValue());
            
            //Set up start and end hours +/- time zone difference
            int startHourDiff = startHour + timeDiff;
            int endHourDiff = endHour + timeDiff;
            //check for times set above to be less than 23 (Must be between 0-23)
            if(startHourDiff > 23){
                startHourDiff = startHourDiff - 24;
            }
            if(endHourDiff > 23){
                endHourDiff = endHourDiff - 24;
            }
            
            //Check for appointment start times and end times being subsequent
            if(startHour > endHour){
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment start time is set after end time.");
                alert.showAndWait();
            } else if(startHour == endHour && startMin >= endMin){
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment start time is set after or equal to end time.\n"
                        + "Appointment must be at least 15 minutes long.");
                alert.showAndWait();
                
            //Check for appointment time being within office hours (8am to 10pm EST)
            } else if(startHourDiff < 8 || endHourDiff < 8){
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment time is set before 8am EST (Office hours 8am-10pm EST).");
                alert.showAndWait();
            } else if(startHourDiff >= 22 || endHourDiff >= 22){
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment time is set after 10pm EST (Office hours 8am-10pm EST).");
                alert.showAndWait();
            } else {
                
            //Check for appointment overlap
                try{
                    Statement stmt;
                    ResultSet rs;
                    
                    stmt = DBConnection.conn.createStatement();
            
                    //Get all appointment start and end times on selected customer
                    rs = stmt.executeQuery("SELECT Start, End FROM appointments WHERE Customer_ID = " + custID);

                    //Boolean for if there's appointment overlap
                    apptOverlap = false;
                    //Store to-be-added start and end time timestamp (converted to UTC)
                    Timestamp apptStartTS = Time.assembleStampUTC(year, month, day, startHour, startMin);
                    Timestamp apptEndTS = Time.assembleStampUTC(year, month, day, endHour, endMin);
                    
                    while (rs.next()){
                        //Get each start and end time of current appointments in database (Stored in UTC)
                        Timestamp startTS = rs.getTimestamp("Start");
                        Timestamp endTS = rs.getTimestamp("End");
                        
                        //Check if appointment start or end time is between startTS and endTS
                        if(apptStartTS.after(startTS) && apptStartTS.before(endTS)){
                            apptOverlap = true;
                        }
                        if(apptEndTS.after(startTS) && apptStartTS.before(endTS)){
                            apptOverlap = true;
                        }
                        //Check if appt start or end time are the same as existing start or end times
                        if(apptEndTS.compareTo(endTS) == 0 || apptStartTS.compareTo(startTS) == 0){
                            apptOverlap = true;
                            //Note: This will still allow a new appointment to start at the same time as a previous appt ends,
                            //or end at the same time as another appointment starts.
                        }
                        
                    }
                    
                }catch(SQLException e){
                    System.out.println("Error getting appointments on selected customer.");
                }
                
                if(apptOverlap == true){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Customer selected for an appointment already has an appointment at this time.\n"
                            + "Please schedule this customer for a different appointment time.");
                    alert.showAndWait();
                } else {
                
            //Insert new customer into database
                try{
                PreparedStatement insStmt = DBConnection.conn.prepareStatement("UPDATE appointments "
                        + "SET Title = ?, "
                        + "Description = ?, "
                        + "Location = ?, "
                        + "Type = ?, "
                        + "Start = ?, "
                        + "End = ?, "
                        + "Last_Update = ?, "
                        + "Last_Updated_By = ?, "
                        + "Customer_ID = ?, "
                        + "User_ID = ?, "
                        + "Contact_ID = ? "
                        + "WHERE Appointment_ID = ?");
                insStmt.setString(1, title);
                insStmt.setString(2, description);
                insStmt.setString(3, location);
                insStmt.setString(4, type);
                insStmt.setTimestamp(5, Time.assembleStampUTC(year, month, day, startHour, startMin)); //Start time
                insStmt.setTimestamp(6, Time.assembleStampUTC(year, month, day, endHour, endMin)); //End time
                insStmt.setString(7, Time.utcNow().toString()); //Last update
                insStmt.setString(8, Appointment.currentUser); //Last updated by (Who is logged in)
                insStmt.setInt(9, custID); //Cust ID
                insStmt.setInt(10, userID); //User ID
                insStmt.setInt(11, contID); //Contact ID
                insStmt.setInt(12, ID);


                insStmt.executeUpdate();

                //Alert to confirm appointment creation successful
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment modified");
                alert.showAndWait();

                //Switch back to CalMenu
                Stage stage;
                Parent scene;
                try{
                    stage = (Stage)((Button)event.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(getClass().getResource("/view/calMenu.fxml"));
                    stage.setScene(new Scene(scene));
                    stage.show();
                } catch (IOException e){
                    System.out.println("Error moving back to calendar page after successful appointment add");
                }

                } catch(SQLException e){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "There was an error creating your appointment, please try again.");
                    alert.showAndWait();
                }

            }
        }
        }
    }

    public void initialize(URL url, ResourceBundle rb) {
        //Create lists with hours 00-23 and mins 00-45 in increments of 15
        ObservableList hours = FXCollections.observableArrayList();
        ObservableList mins = FXCollections.observableArrayList();
        
        hours.add("00"); hours.add("01"); hours.add("02"); hours.add("03"); 
        hours.add("04"); hours.add("05"); hours.add("06"); hours.add("07");
        hours.add("08"); hours.add("09"); hours.add("10"); hours.add("11");
        hours.add("12"); hours.add("13"); hours.add("14"); hours.add("15");
        hours.add("16"); hours.add("17"); hours.add("18"); hours.add("19");
        hours.add("20"); hours.add("21"); hours.add("22"); hours.add("23");
        
        mins.add("00"); mins.add("15"); mins.add("30"); mins.add("45"); 
        //Populate comboboxes for hours and minutes, both start and end times
        createApptStartHour.getItems().addAll(hours);
        createApptStartMin.getItems().addAll(mins);
        createApptEndHour.getItems().addAll(hours);
        createApptEndMin.getItems().addAll(mins);
        
        //Fill ID text with highest appt ID + 1
        createApptIDTxt.setText(String.valueOf(Appointment.apptNumber() + 1));
        
        //Populate contact combobox
        try{
            Statement stmt;
            ResultSet rs;
            
            stmt = DBConnection.conn.createStatement();

            rs = stmt.executeQuery("SELECT * FROM contacts");
            contactPairs.clear(); //Clear contactPairs when opening CreateApptMenu to avoid duplicate data
            
            while (rs.next()){
                createApptContCmb.getItems().add(rs.getString("Contact_Name")); //Add each contact to combobox
                contactPairs.put(rs.getString("Contact_Name"), rs.getInt("Contact_ID")); //Store each contact and ID in hashmap
                contactPairsRev.put(rs.getInt("Contact_ID"), rs.getString("Contact_Name")); //And store the reverse
            }
            
        } catch(SQLException e){
            System.out.println("Error getting contacts for ComboBox");
        }
        
        //Populate Customer combobox
        try{
            Statement stmt;
            ResultSet rs;
            
            stmt = DBConnection.conn.createStatement();

            rs = stmt.executeQuery("SELECT * FROM customers");
            custPairs.clear(); //Clear countryPairs when opening custMenu to avoid duplicate data
            
            while (rs.next()){
                createApptCustCmb.getItems().add(rs.getString("Customer_Name")); //Add each customer to combobox
                custPairs.put(rs.getString("Customer_Name"), rs.getInt("Customer_ID")); //Store each customer and ID in hashmap
                custPairsRev.put(rs.getInt("Customer_ID"), rs.getString("Customer_Name")); //And store the reverse
            }
            
        } catch(SQLException e){
            System.out.println("Error getting contacts for ComboBox");
        }
        
        //Populate User combobox
        try{
            Statement stmt;
            ResultSet rs;
            
            stmt = DBConnection.conn.createStatement();
            
            rs = stmt.executeQuery("SELECT * FROM users");
            userPairs.clear();
            
            while (rs.next()){
                createApptUserCmb.getItems().add(rs.getString("User_Name")); //Add each user to combobox
                userPairs.put(rs.getString("User_Name"), rs.getInt("User_ID")); //Store each user and ID in hashmap
                userPairsRev.put(rs.getInt("User_ID"), rs.getString("User_Name")); //And store the reverse
            } 
            
        }catch(SQLException e){
            System.out.println("Error getting users for Combobox");
        }
        
        //Fill textfields with old Appt values
        createApptIDTxt.setText(String.valueOf(oldAppt.getApptID()));
        createApptTitleTxt.setText(oldAppt.getTitle());
        createApptDescTxt.setText(oldAppt.getDescription());
        createApptLocTxt.setText(oldAppt.getLocation());
        createApptTypeTxt.setText(oldAppt.getType());
        
        //Populate contact, customer, and user comboboxes with reversed hashmap values
        createApptContCmb.setValue(contactPairsRev.get(oldAppt.getContID()));
        createApptCustCmb.setValue(custPairsRev.get(oldAppt.getCustID()));
        createApptUserCmb.setValue(userPairsRev.get(oldAppt.getUserID()));
        
        //Populate DatePicker
        createApptDatePick.setValue(oldAppt.getStart().toLocalDateTime().toLocalDate());
        
        //Populate Start and End time hour and minute comboboxes
        createApptStartHour.setValue(String.valueOf(oldAppt.getStart().getHours()));
        createApptStartMin.setValue(String.valueOf(oldAppt.getStart().getMinutes()));
        createApptEndHour.setValue(String.valueOf(oldAppt.getEnd().getHours()));
        createApptEndMin.setValue(String.valueOf(oldAppt.getEnd().getMinutes()));
    }
    
}