import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class which includes the form to collect the users input on what application should be added to the software,
 * including the user, contact, customer, and appointment information, along with the date, which is checked against
 * restraints such as overlapping appointment for a customer,
 *
 @author Lucas Hynes
 @version 1.0
 @since  11/20/2020
 */

public class AddAppointmentForm {
    /**
     * Is the main function call for the class to return a scene
     *
     * @param user allows the user to be passed through and added to the database
     * @return Scene containing the layout and function calls to be able to update the given database with the correct
     * appointment information
     * @throws SQLException thrown due to error in input
     */
    public Scene addAppointment(User user) throws SQLException {
        //returns the country and language of the computer to know what settings to return for the user
        Locale user_local = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", user_local);

        //allows access to change the database
        DBAccess data = new DBAccess();

        //Labels for GUI to inform user of what input to enter into the form
        //Label layout for appointment
        Label appointmentIdLabel = new Label(messages.getString("appointmentLabel"));
        appointmentIdLabel.setPadding(new Insets(5,5,5,5));

        //Label layout for title
        Label titleLabel = new Label(messages.getString("titleLabel"));
        titleLabel.setPadding(new Insets(5,5,5,5));

        //Label layout for description
        Label descriptionLabel = new Label(messages.getString("descriptionLabel"));
        descriptionLabel.setPadding(new Insets(5,5,5,5));

        //Label layout for location
        Label locationLabel = new Label(messages.getString("locationLabel"));
        locationLabel.setPadding(new Insets(5,5,5,5));

        //Label layout for contact
        Label contactLabel = new Label(messages.getString("contactLabel"));
        contactLabel.setPadding(new Insets(5,5,5,5));

        //Label layout for type
        Label typeLabel = new Label(messages.getString("typeLabel"));
        typeLabel.setPadding(new Insets(5,5,5,5));

        //Label layout for start time and date
        Label startTimeLabel = new Label(messages.getString("startLabel"));
        startTimeLabel.setPadding(new Insets(5,5,5,5));

        //Label layout for end time and date
        Label endTimeLabel = new Label(messages.getString("endLabel"));
        endTimeLabel.setPadding(new Insets(5,5,5,5));

        //Label layout for customer
        Label customerLabel = new Label(messages.getString("customerLabel"));
        customerLabel.setPadding(new Insets(5,5,5,5));

        //Label layout for customer
        Label userLabel = new Label(messages.getString("userLabel"));
        userLabel.setPadding(new Insets(5,5,5,5));

        //sets text field to not be able to be edited by the user
        TextField appointmentIdField = new TextField(messages.getString("idEditDisabled"));
        appointmentIdField.setEditable(false);

        //sets the empty text fields for the user to input field values
        TextField titleField = new TextField();
        TextField descriptionField = new TextField();
        TextField typeField = new TextField();
        TextField locationField = new TextField();
        //sets a prompt to inform users of the format to express time
        TextField startTimeField = new TextField("HH:mm:ss");
        TextField endTimeField = new TextField("HH:mm:ss");

        //picks the date for the appointment to be based off of
        DatePicker datePicker = new DatePicker();

        //allows the user to select contact based off of drop down list
        ComboBox contactChoice = new ComboBox<>();
        contactChoice.getItems().addAll(data.getContactNameArray());

        //allows the user to select the customer based off of drop down list
        ComboBox customerChoice = new ComboBox<>();
        customerChoice.getItems().addAll(data.getCustomerNameArray());

        ComboBox userChoice = new ComboBox<>();
        userChoice.getItems().addAll(data.getUserNameArray());

        //initializes buttons for the user to navigate through the pages
        Button saveButton = new Button(messages.getString("saveLabel"));
        Button cancelButton = new Button(messages.getString("cancelLabel"));

         //Event Handler for the Save Button
        EventHandler<ActionEvent> saveEvent = e -> {
            try {
                //creates the correct format for the times to be entered into the database
                String startTime = datePicker.getValue() + "T" + startTimeField.getText() + OffsetDateTime.now().getOffset() + "["+ ZoneId.systemDefault() + "]";
                String endTime = datePicker.getValue() + "T" + endTimeField.getText() + OffsetDateTime.now().getOffset() + "[" + ZoneId.systemDefault() + "]";

                //then converts the times in format to the zone at which it was made
                ZonedDateTime start = ZonedDateTime.parse(startTime);
                ZonedDateTime end = ZonedDateTime.parse(endTime);

                //checks against the business hours to take the first step in validating the input
                if (start.withZoneSameInstant(ZoneId.of("America/New_York")).toLocalTime().isBefore(LocalTime.parse("08:00:00"))
                    || (end.withZoneSameInstant(ZoneId.of("America/New_York")).toLocalDateTime().isAfter(LocalDateTime.parse(end.toLocalDate() + "T22:00:00")))){
                    //if the appointment is outside of business hours, display the error code frame
                        Stage stageTwo = new Stage();
                        stageTwo.setScene(errorUserTimeSet());
                        stageTwo.show();
                } else {
                    //if it is within the business hours, checks to make sure that the customer that is assigned to
                    //the appointment does not have any overlapping appointments
                    if(overlapCheck(start, end, customerChoice.getSelectionModel().getSelectedItem().toString())) {
                        //takes the users input and attempts to apply it as a new appointment
                        data.addAppointment(titleField.getText(), descriptionField.getText(), locationField.getText(),
                                data.getContactID(contactChoice.getSelectionModel().getSelectedItem().toString()),
                                typeField.getText(), start, end,
                                data.getCustomerID(customerChoice.getSelectionModel().getSelectedItem().toString()),
                                data.getUserID(userChoice.getSelectionModel().getSelectedItem().toString()));

                        //first takes the currently open window and closes it
                        Stage stage = (Stage) saveButton.getScene().getWindow();
                        stage.close();

                        //after closing, launches the main form
                        Stage mainStage = new Stage();
                        MainForm main = new MainForm();
                        try {
                            main.setUser(user);
                            main.start(mainStage);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }

                    else {
                        //if the appointment that was attempted to be saved is outside of the given business hours,
                        //returns error message to the user
                        Stage stageTwo = new Stage();
                        stageTwo.setScene(errorUserOverlap());
                        stageTwo.show();
                    }
                }
                //catches the error calls and returns info to console
            } catch(Exception throwables){
                throwables.printStackTrace();
            }
        };
        // Set on action
        saveButton.setOnAction(saveEvent);

        //event handler to return the user to the main stage
        EventHandler<ActionEvent> cancelEvent = e -> {
            //closes the add appointment window
            Stage stage = (Stage)cancelButton.getScene().getWindow();
            stage.close();

            //after closing, launches the main form
            Stage mainStage = new Stage();
            MainForm main = new MainForm();
            try {
                main.setUser(user);
                main.start(mainStage);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };
        //Set on action
        cancelButton.setOnAction(cancelEvent);

        //sets the layout for the field and label values for the user.
        GridPane userInputLayout = new GridPane();
        //appointment inout area
        userInputLayout.add(appointmentIdLabel, 0, 0, 1, 1);
        userInputLayout.add(appointmentIdField, 1, 0, 1, 1);
        //title user input
        userInputLayout.add(titleLabel, 0, 1, 1, 1);
        userInputLayout.add(titleField, 1, 1, 1, 1);
        //description user input
        userInputLayout.add(descriptionLabel, 0, 2, 1, 1);
        userInputLayout.add(descriptionField, 1, 2, 1, 1);
        //location user input
        userInputLayout.add(locationLabel, 0, 3, 1, 1);
        userInputLayout.add(locationField, 1, 3, 1, 1);
        //type user input
        userInputLayout.add(typeLabel, 0, 4, 1, 1);
        userInputLayout.add(typeField, 1, 4, 1, 1);
        //start time and date user input
        userInputLayout.add(startTimeLabel, 0, 5, 1, 1);
        userInputLayout.add(startTimeField, 1, 5, 1, 1);
        //end time and dta
        userInputLayout.add(endTimeLabel, 0, 6, 1, 1);
        userInputLayout.add(endTimeField, 1, 6, 1, 1);
        //sets the date picker to be near the start and end fields
        userInputLayout.add(datePicker, 2, 5, 2, 2);
        //contact user input
        userInputLayout.add(contactLabel, 0, 7, 1, 1);
        userInputLayout.add(contactChoice, 1, 7, 1, 1);
        //customer user input
        userInputLayout.add(customerLabel, 0, 8, 1, 1);
        userInputLayout.add(customerChoice, 1, 8, 1, 1);
        //user's user input
        userInputLayout.add(userLabel, 0, 9, 1, 1);
        userInputLayout.add(userChoice, 1, 9, 1, 1);
        //sets the gaps within the grid pane
        userInputLayout.setHgap(20);
        userInputLayout.setVgap(20);

        //sets the layout for the buttons to navigate through the application
        HBox buttons = new HBox(saveButton, cancelButton);
        buttons.setPadding(new Insets(10,10,10,10));

        //then combines the fields to make the final layout and adding the padding
        VBox finalLayout = new VBox(userInputLayout, buttons);
        finalLayout.setPadding(new Insets(10,10,10,10));

        //returns a scene with the layout of finalLayout
        return new Scene(finalLayout);
    }

    /**
     * used to display to user that the time entered for the appointment has made it with the customer having an
     * overlapping appointment already set
     * @return popup window displaying error info
     */
    private Scene errorUserOverlap() {
        //sets the location and language to the default set on the users computer
        Locale user_local = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", user_local);

        //creates the label to display the error information
        Label errorMessage = new Label(messages.getString("overlapError"));

        //creates the button to exit out of the pop up window
        Button okButton = new Button(messages.getString("okLabel"));

        //adds event handler to be able to close down the window
        EventHandler<ActionEvent> okEvent = e -> ((Stage)okButton.getScene().getWindow()).close();
        //applies the event to the button
        okButton.setOnAction(okEvent);

        //sets the format and returns the scene
        HBox format = new HBox(errorMessage, okButton);
        format.setSpacing(10);
        format.setPadding(new Insets(10, 10, 10, 10));
        return new Scene(format);
    }

    /**
     * checks to see if the selected customer has an associated appointment overlapping with the users preliminary
     * appointment request
     * @param start the start time and date of the appointment
     * @param end the end time and date of the appointment
     * @param CustomerName the customer assigned
     * @return boolean value, true means no overlap, false means overlap
     * @throws SQLException protect based off of improper layout
     */
    private boolean overlapCheck(ZonedDateTime start, ZonedDateTime end, String CustomerName) throws SQLException {
        //checks to first see if the times set are possible to do with the start before the end
        if(end.isBefore(start)){return false;}
        //initializes database access
        DBAccess data = new DBAccess();

        //creates total list of the customers appointments
        ObservableList<LocalDateTime[]> customerAppTimes = data.customerAssociatedAppointments(
                data.getCustomerID(CustomerName));

        //loops through the appointments
        for (LocalDateTime[] customerAppTime : customerAppTimes) {
            //checks to see if there is an appointment within the same day
            if(start.getDayOfYear() == customerAppTime[1].getDayOfYear()) {
                //checks to see if the appointment times are able to fit between other given values or over the values
                //or there is another form of overlap whether that be the same time as another appointment etc.
                if (((((start.withZoneSameInstant(ZoneId.of("UTC")).isBefore(ZonedDateTime.of(customerAppTime[1], ZoneId.of("UTC")))) &&
                        (start.withZoneSameInstant(ZoneId.of("UTC")).isAfter(ZonedDateTime.of(customerAppTime[0], ZoneId.of("UTC")))))
                        || ((end.withZoneSameInstant(ZoneId.of("UTC")).isAfter(ZonedDateTime.of(customerAppTime[0], ZoneId.of("UTC")))) &&
                        end.withZoneSameInstant(ZoneId.of("UTC")).isBefore(ZonedDateTime.of(customerAppTime[1], ZoneId.of("UTC"))))) ||
                        ((start.withZoneSameInstant(ZoneId.of("UTC")).toLocalTime() == customerAppTime[0].toLocalTime()) ||
                        (end.withZoneSameInstant(ZoneId.of("UTC")).toLocalTime() == customerAppTime[1].toLocalTime()))) ||
                        ((start.withZoneSameInstant(ZoneId.of("UTC")).isBefore(ZonedDateTime.of(customerAppTime[0], ZoneId.of("UTC"))) &&
                        (end.withZoneSameInstant(ZoneId.of("UTC")).isAfter(ZonedDateTime.of(customerAppTime[1], ZoneId.of("UTC"))))))){
                    return false;
                }
            }
        }
        //goes through loop and no matches is able to return true, no overlap
        return true;
    }

    /**
     * returns a pop up window informing user that the time set by the user has resulted in an error
     * @return the pop up window scene
     */
    private Scene errorUserTimeSet() {
        //sets the proper language and country info to display properly formatted information
        Locale user_local = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", user_local);

        //sets the error message label for the business hours error
        Label errorMessage = new Label(messages.getString("timeSelectErrorCode"));

        //sets the button for the user to navigate through program
        Button okButton = new Button(messages.getString("okLabel"));

        //sets the event handler to be able to close the pop up window
        EventHandler<ActionEvent> okEvent = e -> ((Stage)okButton.getScene().getWindow()).close();
        //applies event to the button
        okButton.setOnAction(okEvent);

        //formats the message and returns the error
        HBox format = new HBox(errorMessage, okButton);
        format.setSpacing(10);
        format.setPadding(new Insets(10, 10, 10, 10));
        return new Scene(format);
    }
}
