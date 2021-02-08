import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.sql.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class for the login stage of the application to verify the user based on a username and a password
 * @author Lucas Hynes
 * @version 1.0
 * @since 11/20/2020
 */
public class LoginStage extends Application {

    /**
     * the main launch method for the application
     * @param args argument array for the main call
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * the main form application code for the login scene
     * @param stage the primary stage of the window
     */
    public void start(Stage stage) {
        //sets the language and the location for the default system settings of the user
        Locale user_local = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", user_local);

        //sets the access to the database method calls
        DBAccess data = new DBAccess();

        //displays the username label
        Label userNamePrompt = new Label(messages.getString("userNameLabel"));

        //displays the password label
        Label passwordPrompt = new Label(messages.getString("passwordLabel"));

        //displays the location label
        Label locationLabel = new Label(messages.getString("locationLoginLabel"));

        //displays the location of the user
        Label locationUser = new Label(data.getZonedTime().getZone().toString());

        //text prompt for username input field
        TextField userNameInput = new TextField();
        userNameInput.setPromptText(messages.getString("userNamePromptLabel"));

        //password field prompt
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText(messages.getString("passwordPromptLabel"));

        //creates the login button
        Button loginButton = new Button(messages.getString("loginLabel"));

        //creates the event handler for the login button
        EventHandler<ActionEvent> loginButtonEvent = e -> {
            try {
                //creates a local time variable
                LocalDateTime ldt = LocalDateTime.now();

                //creates a time and date format to store information
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                //creates timestamp of the current time
                Timestamp ts = Timestamp.valueOf(ldt.format(dtf));

                //creates variable to hold login information to the log
                String info;

                //returns the user id (-1 val if the login attempt failed
                int userId = data.checkLogin(userNameInput.getText(), passwordInput.getText());

                //checks if the login was a success
                if(userId != -1) {
                    //launches the main form for the user due to the successful login
                    MainForm main = new MainForm();

                    //sets the user to the login attempted success values
                    User user = new User(userId, userNameInput.getText());

                    //starts the main form after setting the user
                    main.setUser(user);
                    main.start(stage);

                    //records the login information into the string to be written into the file
                    info = "Username: " + userNameInput.getText() + "\t Date/Time:" + ts + "\t Login Successful";
                }
                else {
                    //records the login attempt information to be written into the file
                    info = "Username: " + userNameInput.getText() + "\t Date/Time:" + ts + "\t Login Failed";

                    //displays info to user about not being able to login
                    Label failLogin = new Label(messages.getString("failLogin"));
                    //button to navigate
                    Button okButton = new Button(messages.getString("okLabel"));

                    //event handler to close pop-up
                    EventHandler<ActionEvent> okEvent = e1 -> ((Stage)okButton.getScene().getWindow()).close();
                    okButton.setOnAction(okEvent);

                    //layout for the pop up
                    VBox popUpLayout = new VBox(failLogin, okButton);
                    popUpLayout.setSpacing(10);
                    popUpLayout.setPadding(new Insets(10, 10, 10, 10));

                    //sets stage to display
                    Scene layout = new Scene(popUpLayout);
                    Stage stage2 = new Stage();
                    stage2.setScene(layout);
                    stage2.show();
                }

                //sets the file writer to the log file
                File file = new File("src/login_activity.txt");
                FileWriter logAppending = new FileWriter(file, true);

                //sets the string to the login file
                logAppending.write("\n" + info);

                //closes the file after the write
                logAppending.close();
            } catch (SQLException | IOException throwables) {
                //catches any invalid user input
                throwables.printStackTrace();
            }
        };
        //adds the button to the event handler
        loginButton.setOnAction(loginButtonEvent);

        //grid pane for the login information
        GridPane loginInfo = new GridPane();
        //adds the username field section
        loginInfo.add(userNamePrompt, 0, 0, 1, 1);
        loginInfo.add(userNameInput, 1, 0, 1, 1);
        //adds the password field section
        loginInfo.add(passwordPrompt, 0, 1, 1, 1);
        loginInfo.add(passwordInput, 1, 1, 1, 1);
        //adds the location field section
        loginInfo.add(locationLabel, 0, 2, 1, 1);
        loginInfo.add(locationUser, 1, 2, 1 ,1);
        //settings of the grid pane
        loginInfo.setHgap(10);
        loginInfo.setVgap(10);

        //sets the layout for the final window
        VBox finalGroup = new VBox(loginInfo, loginButton);
        //sets the settings for the window
        finalGroup.setSpacing(10);
        finalGroup.setPadding(new Insets(10, 10, 10, 10));
        finalGroup.setAlignment(Pos.CENTER);

        //sets the title for the scheduling application
        stage.setTitle(messages.getString("stageTitleLabel"));
        stage.setWidth(500);
        stage.setHeight(200);

        //sets the scene for the login form
        Scene scene = new Scene(finalGroup);
        stage.setScene(scene);
        stage.show();
    }
}
