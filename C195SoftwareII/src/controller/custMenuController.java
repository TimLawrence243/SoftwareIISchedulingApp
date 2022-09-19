/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.Appointment;
import model.Customer;
import model.Time;
import utils.DBConnection;

/**
 * Controller class for Customer Menu.
 * Contains methods used for adding, updating, deleting customers, as well as displaying
 * data for already existing customers.  Data is obtained from, added to, and updated within
 * the database.
 * @author tim83
 */
public class custMenuController implements Initializable {

    Stage stage;
    Parent scene;
    int custNumber;
    
    @FXML
    private Button custBackBtn;

    @FXML
    private TableView<Customer> custTable;

    @FXML
    private TableColumn<Customer, Integer> custIDCol;

    @FXML
    private TableColumn<Customer, String> custNameCol;

    @FXML
    private ComboBox<String> custDivCmb;

    @FXML
    private TextField custNameTxt;

    @FXML
    private TextField custAddressTxt;

    @FXML
    private TextField custPostalTxt;

    @FXML
    private TextField custPhoneTxt;

    @FXML
    private ComboBox<String> custCountryCmb;

    @FXML
    private TextField custIDTxt;

    @FXML
    private Button custCreateBtn;

    @FXML
    private Button custUpdateBtn;

    @FXML
    private Button custDeleteBtn;
    
    @FXML
    private Button newCustBtn;
    
    @FXML
    private Label createSuccessLbl;
    
    //Hashmap for storing which country has which COUNTRY_ID
    Map <String, Integer> countryPairs = new HashMap<String, Integer>();

    /**
     * Populates "division" Combobox relative to what Country was selected
     * @param event "Country" Combobox changed
     */
    @FXML
    void onActionCountrySelected(ActionEvent event) {
        Statement stmt;
        ResultSet rs = null;
        
        
        
        custDivCmb.getItems().clear(); //Clear Division ComboBox
        
        String countrySelected = custCountryCmb.getValue();
        
        try{
            stmt = DBConnection.conn.createStatement();
            
            Integer key = countryPairs.get(countrySelected);
            
            rs = stmt.executeQuery("SELECT * FROM first_level_divisions "
                                 + "WHERE COUNTRY_ID = " + key + ";");
            while (rs.next()){
                custDivCmb.getItems().add(rs.getString("Division")); //Add each division to combobox
                
            } 

        } catch (SQLException e){
            
        }
    }
    
    /**
     * Clear all fields and populate ID column with a new ID.  
     * Make 'Create' button visible and 'Update' invisible.
     * @param event "Create new customer" Button clicked
     */
    @FXML
    void onActionCreateNew(ActionEvent event) {
        //Un-disable boxes (boxes disabled on first entering the screen)
        custNameTxt.setDisable(false);
        custAddressTxt.setDisable(false);
        custPostalTxt.setDisable(false);
        custPhoneTxt.setDisable(false);
        custCountryCmb.setDisable(false);
        custDivCmb.setDisable(false);
        
        //Set appropriate labels and buttons visible
        createSuccessLbl.setVisible(false);
        custCreateBtn.setVisible(true);
        custUpdateBtn.setVisible(false);
        custDeleteBtn.setVisible(false);
        
        //Clear all text and combo boxes for new inputs.  Set ID box to a new number.
        custIDTxt.setText(String.valueOf(custNumber));
        custNameTxt.setText("");
        custAddressTxt.setText("");
        custPostalTxt.setText("");
        custPhoneTxt.setText("");
        custCountryCmb.getSelectionModel().clearSelection();
        custDivCmb.getSelectionModel().clearSelection();
        //Clear Division combobox (Was giving duplicate "Select country first" options in certain circumstances)
        custDivCmb.getItems().clear();
        //Add placeholder to Division combo box
        custDivCmb.getItems().add("Select country first");
    }
    
    /**
     * Adds a new customer to the database
     * Checks that all fields are filled, displays specific alerts if not
     * Adds the customer and displays a label saying 'Customer created', then refreshes the table to reflect new customer
     * @param event Button clicked
     */
    @FXML
    void onActionCreateCust(ActionEvent event) {
        
        int ID = Integer.parseInt(custIDTxt.getText());
        String name = custNameTxt.getText();
        String address = custAddressTxt.getText();
        String postal = custPostalTxt.getText();
        String phone = custPhoneTxt.getText();
        //String country = custCountryCmb.getSelectionModel().getSelectedItem(); Don't need country, just division
        String division = custDivCmb.getSelectionModel().getSelectedItem();
        int divisionID = 0; //Used below
        //Check all fields to make sure they're not empty
        if(name.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Name can not be empty (All fields required)");
            alert.showAndWait();
        } else if(address.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Address can not be empty (All fields required)");
            alert.showAndWait();
        } else if(postal.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Postal code can not be empty (All fields required)");
            alert.showAndWait();
        } else if(phone.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Phone number can not be empty (All fields required)");
            alert.showAndWait();
        } else if(division == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Division can not be empty (All fields required).  Select a country, then select a division within that country.");
            alert.showAndWait();
        } else {
            Statement stmt;
            ResultSet rs = null;
            
            
            //Get division ID from division string input
            try{
                stmt = DBConnection.conn.createStatement();

                rs = stmt.executeQuery("Select Division_ID FROM first_level_divisions"
                        + " WHERE Division = '" + division + "'");
                while(rs.next()){
                    divisionID = rs.getInt("Division_ID");
                }
            } catch(SQLException e){
                System.out.println("Error getting Division ID from Division");
            }
            
            //Insert new customer into database
            try{
            PreparedStatement insStmt = DBConnection.conn.prepareStatement("INSERT INTO customers VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            insStmt.setInt(1, ID);
            insStmt.setString(2, name);
            insStmt.setString(3, address);
            insStmt.setString(4, postal);
            insStmt.setString(5, phone);
            insStmt.setString(6, Time.utcNow().toString());
            insStmt.setString(7, Appointment.currentUser);
            insStmt.setString(8, Time.utcNow().toString());
            insStmt.setString(9, Appointment.currentUser);
            insStmt.setInt(10, divisionID);
            
            
            insStmt.executeUpdate();
            
            Customer.generateAllCusts();
            custTable.setItems(Customer.getAllCust());
            
            custCreateBtn.setVisible(false);
            createSuccessLbl.setVisible(true);
            //Increment custNumber if we're adding multiple customers back-to-back
            //(custNumber is pulled in during initialization from Customer class,
            //and does not update on its own until we go back to calendar page.)
            custNumber++;
            
            
            
            } catch(SQLException e){
                System.out.println("Error adding new customer");
            }
        }
    }
    
    /**
     * Updates currently selected customer with any new information input to text/combo boxes
     * "Update" button is invisible until a customer is selected in the table
     * When customer selected, fields populate with current information.
     * Clicking "Update" button will take whatever info is in text/combo boxes and replace that data on selected customer
     * @param event "Update" button clicked
     */
    @FXML
    void onActionUpdateCust(ActionEvent event) {
        int ID = Integer.parseInt(custIDTxt.getText());
        String name = custNameTxt.getText();
        String address = custAddressTxt.getText();
        String postal = custPostalTxt.getText();
        String phone = custPhoneTxt.getText();
        //String country = custCountryCmb.getSelectionModel().getSelectedItem(); Don't need country, just division
        String division = custDivCmb.getSelectionModel().getSelectedItem();
        int divisionID = 0; //Used below
        
        //Check all fields to make sure they're not empty
        if(name.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Name can not be empty (All fields required)");
            alert.showAndWait();
        } else if(address.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Address can not be empty (All fields required)");
            alert.showAndWait();
        } else if(postal.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Postal code can not be empty (All fields required)");
            alert.showAndWait();
        } else if(phone.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Phone number can not be empty (All fields required)");
            alert.showAndWait();
        } else if(division == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Division can not be empty (All fields required).  Select a country, then select a division within that country.");
            alert.showAndWait();
        } else {
            Statement stmt;
            ResultSet rs = null;
            
            
            //Get division ID from division string input
            try{
                stmt = DBConnection.conn.createStatement();

                rs = stmt.executeQuery("Select Division_ID FROM first_level_divisions"
                        + " WHERE Division = '" + division + "'");
                while(rs.next()){
                    divisionID = rs.getInt("Division_ID");
                }
            } catch(SQLException e){
                System.out.println("Error getting Division ID from Division");
            }
            
            //Insert new customer into database
            try{
            PreparedStatement insStmt = DBConnection.conn.prepareStatement("UPDATE customers "
                    + "SET Customer_ID = ?, "
                    + "Customer_Name = ?, "
                    + "Address = ?, "
                    + "Postal_Code = ?, "
                    + "Phone = ?, "
                    + "Last_Update = ?, "
                    + "Last_updated_By = ?, "
                    + "Division_ID = ? "
                    + "WHERE Customer_ID = ?");
            
            insStmt.setInt(1, ID);
            insStmt.setString(2, name);
            insStmt.setString(3, address);
            insStmt.setString(4, postal);
            insStmt.setString(5, phone);
            insStmt.setString(6, Time.utcNow().toString());
            insStmt.setString(7, Appointment.currentUser);
            //insStmt.setString(8, "NOW()");
            insStmt.setInt(8, divisionID);
            insStmt.setInt(9, ID);
            
            
            insStmt.executeUpdate();
            
            Customer.generateAllCusts();
            custTable.setItems(Customer.getAllCust());
            
            custUpdateBtn.setVisible(false);
            
            
            
            } catch(SQLException e){
                System.out.println("Error updating customer");
            }
        }
    }
    
    /**
     * Deletes the selected customer
     * Prompts user with Y/N dialogue for clarification
     * Deletes customer from database and refreshes table to reflect customer deletion.
     * @param event "DELETE" button clicked
     */
    @FXML
    void onActionDeleteCust(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will DELETE the selected customer - Continue?");
        Optional<ButtonType> option = alert.showAndWait();
        //On clicking 'OK' in confirmation dialogue:
        if(option.get() == ButtonType.OK){
            Statement stmt;
            ResultSet rs = null;
            Statement stmt2;

            int id = Integer.valueOf(custIDTxt.getText());
            
            try{
            stmt = DBConnection.conn.createStatement();
            
            //Select rows in appointments table with the Customer ID to be deleted
            rs = stmt.executeQuery("SELECT * FROM appointments WHERE Customer_ID = " + id);
            
            //If there are rows containing the customer ID, output an alert and do not delete customer.
            if (rs.next() != false){
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION, "Customer has pending appointments, "
                        + "can not delete customer with pending appointments.\n"
                        + "To delete this customer, delete any appointments with this customer from the calendar page.");
                alert2.showAndWait();
            } else {
                stmt2 = DBConnection.conn.createStatement();
            
                //Delete customer
                stmt2.executeUpdate("DELETE FROM customers WHERE Customer_ID = " + id);
                //Refresh table showing customer removal
                Customer.generateAllCusts();
                custTable.setItems(Customer.getAllCust());
            }
            
        } catch(SQLException e){
            System.out.println("Error deleting customer.");
        }
            
        //User pressed 'Cancel' instead of 'OK'  
        } else {
            System.out.println("Cancel");
        }
    }
    
    /**
     * Exit custMenu and returns to Calendar page
     * @param event "Back to Calendar" button clicked
     * @throws IOException 
     */
    @FXML
    void onActionBack(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/calMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }


    /**
     * Initializes the controller class.
     * Sets appropriate buttons and labels to be invisible until needed
     * Populates Country combo box and puts placeholder into Division combo box
     * Populates table with Customer IDs and names
     * Sets up a listener for when a table row is selected
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Statement stmt;
        ResultSet rs = null;
        
        //Set all buttons to be invisible until proper areas are clicked
        createSuccessLbl.setVisible(false);
        custCreateBtn.setVisible(false);
        custUpdateBtn.setVisible(false);
        custDeleteBtn.setVisible(false);
        
        //Populate country combobox
        try{
            stmt = DBConnection.conn.createStatement();

            rs = stmt.executeQuery("SELECT * FROM countries");
            countryPairs.clear(); //Clear countryPairs when opening custMenu to avoid duplicate data
            
            while (rs.next()){
                custCountryCmb.getItems().add(rs.getString("Country")); //Add each country to combobox
                countryPairs.put(rs.getString("Country"), rs.getInt("Country_ID")); //Store each country and ID in hashmap
            }
            
        } catch(SQLException e){
            System.out.println("Error getting countries for ComboBox");
        }
        //Add placeholder to Division combo box
        custDivCmb.getItems().add("Select country first");
        
        
        //Get all customers from database (Done in Customer class)
        Customer.generateAllCusts();
        //Store number of customers for populating ID
        custNumber = Customer.custNumber() + 1;
        
        //Populate table
        custTable.setItems(Customer.getAllCust());
        
        custIDCol.setCellValueFactory(new PropertyValueFactory<>("customer_ID"));
        custNameCol.setCellValueFactory(new PropertyValueFactory<>("customer_Name"));
        
        
        //LISTENER for customer table
        //On selecting a row in the table, populate fields with customer info
        custTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            //Un-disable boxes (boxes disabled on first entering the screen)
            custNameTxt.setDisable(false);
            custAddressTxt.setDisable(false);
            custPostalTxt.setDisable(false);
            custPhoneTxt.setDisable(false);
            custCountryCmb.setDisable(false);
            custDivCmb.setDisable(false);
            
            if (newSelection != null) {
                Customer cust = custTable.getSelectionModel().getSelectedItem();
                
                //Set appropriate buttons to be visible.
                createSuccessLbl.setVisible(false);
                custCreateBtn.setVisible(false);
                custUpdateBtn.setVisible(true);
                custDeleteBtn.setVisible(true);
                
                //Populate text fields
                custIDTxt.setText(String.valueOf(cust.getCustomer_ID()));
                custNameTxt.setText(cust.getCustomer_Name());
                custAddressTxt.setText(cust.getAddress());
                custPostalTxt.setText(cust.getPostal_Code());
                custPhoneTxt.setText(cust.getPhone());
                
                //Populate country and division combo boxes with selected customer's country and division
                Statement stmt2;
                ResultSet rs2 = null;
                int countryID = 0;
                String div = null;
                try{
                    stmt2 = DBConnection.conn.createStatement();

                    rs2 = stmt2.executeQuery("SELECT * FROM first_level_divisions WHERE Division_ID = " + cust.getDivision_ID());
                    //Get country_ID of selected item
                    while (rs2.next()){
                        countryID = rs2.getInt("COUNTRY_ID");
                        div = rs2.getString("Division");
                    }

            } catch(SQLException e){
                System.out.println("Error getting countries for ComboBox");
            }
                
                //Set country combo box as CountryID - 1, because array for combobox starts at 0
                custCountryCmb.getSelectionModel().select(countryID - 1);
                custDivCmb.getSelectionModel().select(div);
                
            }
});
    }    

    
    
}
