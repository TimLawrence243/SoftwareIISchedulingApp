/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195softwareii;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DBConnection;

/**
 * Main method for program.
 * Starts program in MainMenu (the login page) and establishes the connection to 
 * the database, keeping the connection open throughout use of the program.
 * @author tim83
 */
public class C195SoftwareII extends Application {
    
    /**
     * Starts the program on the main login menu page.
     * @param stage
     * @throws IOException 
     */
    @Override
    public void start(Stage stage) throws IOException {
        
        
       Parent root = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
       
       Scene scene = new Scene(root, 900, 400);
       
       
       stage.setScene(scene);
       stage.setTitle("Login");
       stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        DBConnection.startConnection();
        launch(args);
        DBConnection.closeConnection();
    }
    
}
