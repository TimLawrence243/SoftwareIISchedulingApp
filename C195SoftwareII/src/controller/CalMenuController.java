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
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Timestamp;
import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableRow;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Appointment;
import model.Customer;
import utils.DBConnection;

/**
 * FXML Controller class
 *
 * @author tim83
 */
public class CalMenuController implements Initializable {
    Stage stage;
    Parent scene;
    
    @FXML
    private TableView<Appointment> weeklyCalTable;

    @FXML
    private TableColumn<Appointment, Integer> weeklyCalApptIDCol;

    @FXML
    private TableColumn<Appointment, String> weeklyCalTitleCol;

    @FXML
    private TableColumn<Appointment, String> weeklyCalDescCol;

    @FXML
    private TableColumn<Appointment, String> weeklyCalLocCol;

    @FXML
    private TableColumn<Appointment, Integer> weeklyCalContCol;

    @FXML
    private TableColumn<Appointment, String> weeklyCalTypeCol;

    @FXML
    private TableColumn<Appointment, Timestamp> weeklyCalStartCol;

    @FXML
    private TableColumn<Appointment, Timestamp> weeklyCalEndCol;

    @FXML
    private TableColumn<Appointment, Integer> weeklyCalCustIDCol;
    
    @FXML
    private TableView<Appointment> monthlyCalTable;

     @FXML
    private TableColumn<Appointment, Integer> monthlyCalApptIDCol;

    @FXML
    private TableColumn<Appointment, String> monthlyCalTitleCol;

    @FXML
    private TableColumn<Appointment, String> monthlyCalDescCol;

    @FXML
    private TableColumn<Appointment, String> monthlyCalLocCol;

    @FXML
    private TableColumn<Appointment, Integer> monthlyCalContCol;

    @FXML
    private TableColumn<Appointment, String> monthlyCalTypeCol;

    @FXML
    private TableColumn<Appointment, Timestamp> monthlyCalStartCol;

    @FXML
    private TableColumn<Appointment, Timestamp> monthlyCalEndCol;

    @FXML
    private TableColumn<Appointment, Integer> monthlyCalCustIDCol;
    
    

    /**
     * Moves to createApptMenu
     * @param event "Create Appt" Button clicked
     * @throws IOException 
     */
    @FXML
    void onActionAddAppt(ActionEvent event) throws IOException {
        //Create a second window
        //No longer used - Couldn't refresh original CalMenu window after completing appt add.
//        Stage secondStage = new Stage();
//        scene = FXMLLoader.load(getClass().getResource("/view/createApptMenu.fxml"));
//        secondStage.setScene(new Scene(scene));
//        secondStage.show();

        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/createApptMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Moves to custMenu
     * @param event "Customer Maintenance" Button clicked
     * @throws IOException 
     */
    @FXML
    void onActionCustomerMaint(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/custMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Deletes the selected appointment from the database.
     * Gives an alert for Yes/No confirmation, deletes the appointment on clicking "OK" and does nothing on clicking "Cancel"
     * @param event "Delete Appt" button clicked
     */
    @FXML
    void onActionDeleteAppt(ActionEvent event) {
        Appointment apptToDelete = null;
        //Get selected appointment if weekly view is selected
        if(weeklyCalTable.isVisible()){
            apptToDelete = weeklyCalTable.getSelectionModel().getSelectedItem();
        }
        //Get selected appointment if monthly view is selected
        if(monthlyCalTable.isVisible()){
            apptToDelete = monthlyCalTable.getSelectionModel().getSelectedItem();
        }
        
        if(apptToDelete == null || apptToDelete.getApptID() == 0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("No selection - Please select an appointment to delete.");
            alert.showAndWait();
        } else {
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will DELETE the selected appointment - Continue? \n \n"
                + "Appointment ID: " + apptToDelete.getApptID() + ".  \n"
                + "Appointment Type: " + apptToDelete.getType() + ".");
        Optional<ButtonType> option = alert.showAndWait();
        //On clicking 'OK' in confirmation dialogue:
        if(option.get() == ButtonType.OK){
            try{
            Statement stmt;
            
            stmt = DBConnection.conn.createStatement();
            
            //Delete appointment
            stmt.executeUpdate("DELETE FROM appointments WHERE Appointment_ID = " + apptToDelete.getApptID());
            
            //Refresh calendar
            Appointment.generateAllAppts();
            weeklyCalTable.setItems(Appointment.getWeeklyAppts());
            monthlyCalTable.setItems(Appointment.getMonthlyAppts());
            
            
        } catch(SQLException e){
            System.out.println("Error getting contacts for ComboBox");
        }
            
        //User pressed 'Cancel' instead of 'OK'
        } else {
            System.out.println("Cancel");
        }
        }
    }

    /**
     * Takes in appointment selected in weekly view table and moves to modApptMenu.
     * If no appointment is selected, triggers an alert
     * @param event "Modify Appt" button clicked
     * @throws IOException 
     */
    @FXML
    void onActionModAppt(ActionEvent event) throws IOException {
        Appointment apptToSend = null;
        //Take in selected appointment in weekly table
        if(weeklyCalTable.isVisible()){
            apptToSend = weeklyCalTable.getSelectionModel().getSelectedItem();
        }
        if(monthlyCalTable.isVisible()){
            apptToSend = monthlyCalTable.getSelectionModel().getSelectedItem();
        }
        
        //If there's no selection when Modify Appointment button clicked, give an error
        if(apptToSend == null || apptToSend.getApptID() == 0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("No selection - Please select an appointment to modify.");
            alert.showAndWait();
        } else {
            //Store selected appointment for use in pre-populating text fields
            ModApptController.oldAppt = apptToSend;

            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/modApptMenu.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();

        }
        
    }

    /**
     * Sets monthly view table to visible and weekly view table to invisible, if not already so.
     * @param event "Monthly View" button clicked
     */
    @FXML
    void onActionMonthlyView(ActionEvent event) {
        //If the weekly table is visible, and monthly table is not, change to monthly view
        if(weeklyCalTable.isVisible() && !monthlyCalTable.isVisible()){
            weeklyCalTable.setVisible(false);
            monthlyCalTable.setVisible(true);
            //Without this line, monthly table will be behind weekly table, and un-selectable.
            monthlyCalTable.toFront();
        } //Else, do nothing
    }

    /**
     * Sets weekly view table to visible and monthly view table to invisible, if not already so.
     * @param event "Weekly View" button clicked
     */
    @FXML
    void onActionWeeklyView(ActionEvent event) {
        //If the monthly table is visible, and weekly table is not, change to monthly view
        if(monthlyCalTable.isVisible() && !weeklyCalTable.isVisible()){
            monthlyCalTable.setVisible(false);
            weeklyCalTable.setVisible(true);
        } //Else, do nothing
    }
    
    /**
     * Moves to ReportsMenu
     * @param event "Reports" button clicked
     * @throws IOException 
     */
    @FXML
    void onActionReports(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/ReportsMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }
    
    /**
     * Exits the program.
     * @param event "Exit" Button clicked
     */
    @FXML
    private void onActionExit(ActionEvent event) {
        System.exit(0);
    }
    
    
    
    /**
     * Initializes the controller class.
     * Generates weekly and monthly appointments and displays them in the appropriate table views.
     * Checks if there is an appointment within the next 15 minutes and displays an alert if so.
     * LAMBDA -- Lambda is utilized to iterate through the weekly table rows and change the background color of each 
     * row in order to distinguish between days of the week and appointments
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Get ALL appointments from the database and put them into their weekly and monthly Lists (In Appointment class)
        Appointment.generateAllAppts();
        
        //Set up weekly appointments
        weeklyCalTable.setItems(Appointment.getWeeklyAppts());
        //Populate weekly tableview columns
        weeklyCalApptIDCol.setCellValueFactory(new PropertyValueFactory<>("apptID"));
        weeklyCalTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        weeklyCalDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        weeklyCalLocCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        weeklyCalContCol.setCellValueFactory(new PropertyValueFactory<>("custID"));
        weeklyCalTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        weeklyCalStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        weeklyCalEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        weeklyCalCustIDCol.setCellValueFactory(new PropertyValueFactory<>("custID"));
        
        //LAMBDA
        //Change colors of table rows depending on if the ID is 0 (Day of week, not actual appointment) or >0 (Appointment)
        weeklyCalTable.setRowFactory(column -> new TableRow<Appointment>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null)
                    setStyle(""); //Null means blank rows, ie the rows at the bottom that aren't in use
                else if (item.getApptID() == 0)
                    setStyle("-fx-background-color: #6B99E3;"); //Change color of Day of Week rows (Monday, Tuesday, etc)
                else if (item.getApptID() > 0)
                    setStyle("-fx-background-color: #B2C7E8;"); //Change color of rows with appointments
                else
                    setStyle(""); //Anything else (Shouldn't be needed, but just an extra catch-all)
    }
});
        //Set up monthly appointments
        monthlyCalTable.setItems(Appointment.getMonthlyAppts());
        
        //Populate monthly tableview columns
        monthlyCalApptIDCol.setCellValueFactory(new PropertyValueFactory<>("apptID"));
        monthlyCalTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        monthlyCalDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        monthlyCalLocCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        monthlyCalContCol.setCellValueFactory(new PropertyValueFactory<>("custID"));
        monthlyCalTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        monthlyCalStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        monthlyCalEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        monthlyCalCustIDCol.setCellValueFactory(new PropertyValueFactory<>("custID"));
        
        //Method to check if there's an appointment coming up within 15 minutes
        if(Appointment.initialLogin == true){
            Appointment.checkForFifteen();
            Appointment.initialLogin = false;
        }
        
        
    }    

    
    


    
    
}
