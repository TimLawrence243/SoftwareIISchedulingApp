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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DBConnection;

/**
 *
 * @author tim83
 */
public class Customer {
    
    private int customer_ID;
    private String customer_Name;
    private String address;
    private String postal_Code;
    private String phone;
    private Timestamp create;
    private String created_By;
    private Timestamp last_Update;
    private String last_Updated_By;

    public int getCustomer_ID() {
        return customer_ID;
    }

    public void setCustomer_ID(int customer_ID) {
        this.customer_ID = customer_ID;
    }

    public String getCustomer_Name() {
        return customer_Name;
    }

    public void setCustomer_Name(String customer_Name) {
        this.customer_Name = customer_Name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostal_Code() {
        return postal_Code;
    }

    public void setPostal_Code(String postal_Code) {
        this.postal_Code = postal_Code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getCreate() {
        return create;
    }

    public void setCreate(Timestamp create) {
        this.create = create;
    }

    public String getCreated_By() {
        return created_By;
    }

    public void setCreated_By(String created_By) {
        this.created_By = created_By;
    }

    public Timestamp getLast_Update() {
        return last_Update;
    }

    public void setLast_Update(Timestamp last_Update) {
        this.last_Update = last_Update;
    }

    public String getLast_Updated_By() {
        return last_Updated_By;
    }

    public void setLast_Updated_By(String last_Updated_By) {
        this.last_Updated_By = last_Updated_By;
    }

    public int getDivision_ID() {
        return division_ID;
    }

    public void setDivision_ID(int division_ID) {
        this.division_ID = division_ID;
    }
    private int division_ID;
    
    

    
    
    
    
    public Customer(int customer_ID, String customer_Name, String address, String postal_Code, String phone, Timestamp create, 
            String created_By, Timestamp last_Udpate, String last_Updated_By, int division_ID){
        this.customer_ID = customer_ID;
        this.customer_Name = customer_Name;
        this.address = address;
        this.postal_Code = postal_Code;
        this.phone = phone;
        this.create = create;
        this.created_By = created_By;
        this.last_Update = last_Update;
        this.last_Updated_By = last_Updated_By;
        this.division_ID = division_ID;
    }

    
    private static int highestID;
    private static ObservableList<Customer> allCust = FXCollections.observableArrayList();
    
    /**
     * Reach to Database to get all customers and store them into allCust for easier use
     */
    public static void generateAllCusts() {
        
        Statement stmt;
        ResultSet rs = null;
        highestID = 0;
         
        
        //Clear allCust first so we don't get duplicate data
        allCust.clear();
        
        try{
            stmt = DBConnection.conn.createStatement();
            
            rs = stmt.executeQuery("SELECT * FROM customers");
            
            while (rs.next()){
                //Reset appointment a after each iteration
                Customer c = new Customer(0, null, null, null, null, null, null, null, null, 0);
                //Add all columns from database into a
                c.customer_ID = rs.getInt("Customer_ID");
                c.customer_Name = rs.getString("Customer_Name");
                c.address = rs.getString("Address");
                c.postal_Code = rs.getString("Postal_Code");
                c.phone = rs.getString("Phone");
                c.create = rs.getTimestamp("Create_Date");
                c.created_By = rs.getString("Created_By");
                c.last_Update = rs.getTimestamp("Last_Update");
                c.last_Updated_By = rs.getString("Last_Updated_By");
                c.division_ID = rs.getInt("Division_ID");
                
                //Find the highest customerID in the database for generating new customers
                if(c.customer_ID > highestID){
                    highestID = c.customer_ID;
                }
                
                allCust.add(c);
            }
            
            
        } catch (SQLException e){
            System.out.println("Error adding customer to allCust");
        }
        
    }
    
    /**
     * Get currently stored list of all appointments
     * @return returns allCust ObservableList
     */
    public static ObservableList<Customer> getAllCust(){
        return allCust;
    }
    
    /**
     * While generating all customers, we keep track of the highest customer ID, then increment by one to give a new, unique ID
     * @return returns the highest current customer ID in the database
     */
    public static int custNumber(){
        return highestID;
    }
            
}
