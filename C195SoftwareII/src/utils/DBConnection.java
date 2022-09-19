/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connection utilized to connect to the MySQL database
 * @author tim83
 */
public class DBConnection {
    
    //JDBC URL parts
    private static final String protocol = "JDBC";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//wgudb.ucertify.com/WJ06snq";
    
    //JDBC full URL
    private static final String jdbcURL = protocol + vendorName + ipAddress;
    
    //Driver interface reference
    private static final String mySQLJDBCDriver = "com.mysql.jdbc.Driver";
    public static Connection conn = null;
    
    private static final String username = "U06snq"; //Username
    private static final String password = "53688858987"; //Password
    
    public static Connection startConnection(){
        try{
            Class.forName(mySQLJDBCDriver);
            conn = (Connection)DriverManager.getConnection(jdbcURL, username, password);
            
            System.out.println("Connection successful");
        } catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
        return conn;
    
    }
    /**
     * Closes the connection to database (Used when closing program)
     */
    public static void closeConnection(){
        try{
            conn.close();
            System.out.println("Connection closed");
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
} 
