

package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import utils.DBConnection;


/**
 * FXML Controller class for Main Login form.
 *
 * @author tim83
 */
public class MainMenuController implements Initializable {

    Stage stage;
    Parent scene;
    ResourceBundle lang = ResourceBundle.getBundle("language_files/rb", Locale.getDefault());
    
    
    @FXML
    private TextField mainUserTxt;

    @FXML
    private TextField mainPassTxt;
    
    @FXML
    private Button mainLoginBtn;
    @FXML
    private Button mainExitBtn;
    
    @FXML
    private Label ZoneLbl;
    @FXML
    private Label UsernameLbl;

    @FXML
    private Label PasswordLbl;
    
    /**
     * Takes username and password text fields and compares them to the database.
     * If username and password match a database entry, moves on to calender page.
     * If username and password do not match, or there are no usernames that match what was entered, gives error message, and does not move on.
     * Upon successful login, stores Username and UserID for use later in program (i.e. adding a new customer and filling in the "last updated by" column)
     * Finally, regardless of success or failure, appends to text document at root folder titled "login_activity.txt" giving time of login attempt
     * and whether it was successful or not.
     * @param event Login button clicked
     */
    @FXML
    void onActionLogin(ActionEvent event) {
        String username = mainUserTxt.getText();
        String password = mainPassTxt.getText();
        String success = "";
        boolean userMatch = false;
        
        
        Statement stmt;
        ResultSet rs = null;
        
        
        
        try{
            stmt = DBConnection.conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM users"); //Selecting all usernames from users table
            
            while(rs.next()){ //For each username, check it against what is typed into 'username' txtbox
                String user = rs.getString("User_Name");
                if(user.equals(username)){ //If username matches one in database, check if password matches
                    if(password.equals(rs.getString("Password"))){
                        
                        //Translate alert to french if in french
                        if(Locale.getDefault().getLanguage().equals("fr")){
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, lang.getString("Success"));
                            alert.showAndWait();
                        } else { //Do not translate, display default English
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Login success!");
                            alert.showAndWait();
                        }
                        
                        
                        //Progress to main calendar screen
                        try{
                            //After successful login confirmed, set current user for storage and access later
                            Appointment.currentUser = user;
                            Appointment.currentUserID = rs.getInt("User_ID");
                            success = "Successful login";
                            userMatch = true;
                            
                            
                            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
                            stage.setTitle("Calendar");
                            scene = FXMLLoader.load(getClass().getResource("/view/calMenu.fxml"));
                            stage.setScene(new Scene(scene));
                            stage.show();
                        } catch (IOException e){
                            System.out.println("Error moving to main calendar page after successful login");
                        }
                        
                    } else { //If password does NOT match
                        success = "Failed login attempt";
                        userMatch = true;
                        
                        //Translate alert to french if in french
                        if(Locale.getDefault().getLanguage().equals("fr")){
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, lang.getString("Failed"));
                            alert.showAndWait();
                        } else { //Do not translate, display default English
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Login failed");
                            alert.showAndWait();
                        }
                    }
                
                 
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        
        //Username did not match any in database
        if(userMatch == false){
            success = "Failed login attempt";
            //Translate alert to french if in french
            if(Locale.getDefault().getLanguage().equals("fr")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION, lang.getString("Failed"));
                alert.showAndWait();
            } else { //Do not translate, display default English
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Login failed");
                alert.showAndWait();
            }
        }
        
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(
                    new File("login_activity.txt"),
                    true /* append = true */));
            pw.append("Login attempt " + now + " \n");
            pw.append("Username entered: " + username + " \n");
            pw.append(success + " \n \n ----------- \n \n");
            pw.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Error logging login attempt to login_activity.txt");
        }
    }
    
    /**
     * Exits the program.
     * @param event exit button clicked.
     */
    @FXML
    void onActionExit(ActionEvent event) {
        System.exit(0);

    }
    
    /**
     * Initializes the controller class.
     * Changes the label at top right of login screen to show the local ZoneID
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //If french, initialize text on all labels and boxes to translated versions
        if(Locale.getDefault().getLanguage().equals("fr")){
            ZoneLbl.setText(lang.getString("Zone") + ": " + ZoneId.systemDefault().toString());
            UsernameLbl.setText(lang.getString("Username"));
            PasswordLbl.setText(lang.getString("Password"));
            mainLoginBtn.setText(lang.getString("Login"));
            mainExitBtn.setText(lang.getString("Exit"));
        } else { //Else, only set up the ZoneLbl
            ZoneLbl.setText("Zone: " + ZoneId.systemDefault().toString());
        }
        
        
                
    }    
    
}
