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
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The class form for modifying a selected appointment saved into the database
 * @author Lucas Hynes
 * @version 1.0
 * @since 11/20/2020
 */
public class ModifyAppointmentForm {

    /**
     * the main scene function for being able to modify a selected appointment
     * @param selectedAppointment the appointment to be modified
     * @param  user the user to keep identified through out the program
     * @return the scene with input capabilities to be able to edit the appointments
     * @throws SQLException any invalid user input protection
     */
    public Scene modifyAppointment(Appointment selectedAppointment, User user) throws SQLException {
        //sets the language and location for the form based off of user system settings
        Locale user_local = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", user_local);

        //adds access to database method calls
        DBAccess data = new DBAccess();

        //adds the label for the appointment id
        Label appointmentIdLabel = new Label(messages.getString("appointmentLabel"));

        //adds the label for the appointment title
        Label titleLabel = new Label(messages.getString("titleLabel"));

        //adds the label for the appointment description
        Label descriptionLabel = new Label(messages.getString("descriptionLabel"));

        //adds the label for the appointment location
        Label locationLabel = new Label(messages.getString("locationLabel"));

        //adds the label for the appointment contact
        Label contactLabel = new Label(messages.getString("contactLabel"));

        //adds the label for the appointment type
        Label typeLabel = new Label(messages.getString("typeLabel"));

        //adds the label for the appointment start time
        Label startTimeLabel = new Label(messages.getString("startLabel"));

        //adds the label fot the appointment end time
        Label endTimeLabel = new Label(messages.getString("endLabel"));

        //adds the label for the appointment customer
        Label customerLabel = new Label(messages.getString("customerLabel"));

        //Label layout for customer
        Label userLabel = new Label(messages.getString("userLabel"));

        //adds the text field for the appointment id, and sets the text field to not editable
        TextField appointmentIdField = new TextField(String.valueOf(selectedAppointment.getAppointment_ID()));
        appointmentIdField.setEditable(false);

        //sets the text fields for the user input
        TextField titleField = new TextField(selectedAppointment.getTitle());
        TextField descriptionField = new TextField(selectedAppointment.getDescription());
        TextField typeField = new TextField(selectedAppointment.getType());
        TextField locationField = new TextField(selectedAppointment.getLocation());
        TextField startTimeField = new TextField(selectedAppointment.getStart().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        TextField endTimeField = new TextField(selectedAppointment.getEnd().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        //sets the date picker for the appointment
        DatePicker datePicker = new DatePicker(selectedAppointment.getStart().toLocalDate());

        //combo box for selecting the contact for the appointment
        ComboBox contactChoice = new ComboBox();
        //adds the contacts from the database to the combo box
        contactChoice.getItems().addAll(data.getContactNameArray());
        contactChoice.getSelectionModel().select(data.getContactName(selectedAppointment.getContact_ID()));

        //combo box for selecting the customer for the appointment
        ComboBox customerChoice = new ComboBox();
        //adds the customers from the database to the combo box
        customerChoice.getItems().addAll(data.getCustomerNameArray());
        customerChoice.getSelectionModel().select(data.getCustomerName(selectedAppointment.getCustomer_ID()));


        ComboBox userChoice = new ComboBox<>();
        userChoice.getItems().addAll(data.getUserNameArray());
        userChoice.getSelectionModel().select(data.getUserName(selectedAppointment.getUser_ID()));

        //creates the save button for the user to navigate
        Button saveButton = new Button(messages.getString("saveLabel"));

        //creates the cancel button for the user to navigate
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
                    //sets the stage to tell user of the improper input
                    Stage stageTwo = new Stage();
                    stageTwo.setScene(errorUserTimeSet());
                    //shows the error pop up window
                    stageTwo.show();
                }
                //if the appointment has an acceptable assigned time amount
                else {
                    if(overlapCheck(start, end, customerChoice.getSelectionModel().getSelectedItem().toString(), selectedAppointment.getStart(), selectedAppointment.getEnd())) {
                        //calls the database method to change the appointment information to the newly assigned values
                        data.modifyAppointment(selectedAppointment.getAppointment_ID(), titleField.getText(),
                                descriptionField.getText(), locationField.getText(),
                                data.getContactID(contactChoice.getSelectionModel().getSelectedItem().toString()),
                                typeField.getText(), start, end,
                                data.getCustomerID(customerChoice.getSelectionModel().getSelectedItem().toString()),
                                data.getUserID(userChoice.getSelectionModel().getSelectedItem().toString()));

                        //gets the active window and closes it
                        Stage stage = (Stage) saveButton.getScene().getWindow();
                        stage.close();

                        //after closing, launches the main form
                        Stage mainStage = new Stage();
                        MainForm main = new MainForm();
                        try {
                            main.start(mainStage);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    } else {
                        //if the appointment that was attempted to be saved is outside of the given business hours,
                        //returns error message to the user
                        Stage stageTwo = new Stage();
                        stageTwo.setScene(errorUserOverlap());
                        stageTwo.show();
                    }
                }
            } catch (SQLException throwables) {
                //catches any improper input from the collection form
                throwables.printStackTrace();
            }
        };
        // Set on action
        saveButton.setOnAction(saveEvent);

        //sets the event handler for the cancel button to be able to close
        EventHandler<ActionEvent> cancelEvent = e -> {
            ((Stage)cancelButton.getScene().getWindow()).close();

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

        //sets the grid pane for all of the different fields for the user to be able to edit
        GridPane userInputLayout = new GridPane();
        //field layout for appointment id
        userInputLayout.add(appointmentIdLabel, 0, 0, 1, 1);
        userInputLayout.add(appointmentIdField, 1, 0, 1, 1);
        //field layout for appointment title
        userInputLayout.add(titleLabel, 0, 1, 1, 1);
        userInputLayout.add(titleField, 1, 1, 1, 1);
        //field layout for description
        userInputLayout.add(descriptionLabel, 0, 2, 1, 1);
        userInputLayout.add(descriptionField, 1, 2, 1, 1);
        //field layout for location
        userInputLayout.add(locationLabel, 0, 3, 1, 1);
        userInputLayout.add(locationField, 1, 3, 1, 1);
        //field layout for type
        userInputLayout.add(typeLabel, 0, 4, 1, 1);
        userInputLayout.add(typeField, 1, 4, 1, 1);
        //field layout for the start time
        userInputLayout.add(startTimeLabel, 0, 5, 1, 1);
        userInputLayout.add(startTimeField, 1, 5, 1, 1);
        //field layout for the end time
        userInputLayout.add(endTimeLabel, 0, 6, 1, 1);
        userInputLayout.add(endTimeField, 1, 6, 1, 1);
        //layout for the date picker
        userInputLayout.add(datePicker, 2, 6, 2, 2);
        //field layout for the contact
        userInputLayout.add(contactLabel, 0, 7, 1, 1);
        userInputLayout.add(contactChoice, 1, 7, 1, 1);
        //field layout for the customer
        userInputLayout.add(customerLabel, 0, 8, 1, 1);
        userInputLayout.add(customerChoice, 1, 8, 1, 1);
        //user's user input
        userInputLayout.add(userLabel, 0, 9, 1, 1);
        userInputLayout.add(userChoice, 1, 9, 1, 1);
        //settings for the grid pane
        userInputLayout.setHgap(20);
        userInputLayout.setVgap(20);
        userInputLayout.setPadding(new Insets(10, 10, 10, 10));

        //layout for the buttons
        HBox buttons = new HBox(saveButton, cancelButton);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10,10,10,10));

        //combines the buttons layout with the grid pane
        VBox finalLayout = new VBox(userInputLayout, buttons);
        finalLayout.setPadding(new Insets(10,10,10,10));

        //compiles into a scene and returns variable
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
    private boolean overlapCheck(ZonedDateTime start, ZonedDateTime end, String CustomerName, ZonedDateTime originalStart, ZonedDateTime originalEnd) throws SQLException {
        //checks to first see if the times set are possible to do with the start before the end
        if(end.isBefore(start)){return false;}
        //initializes database access
        DBAccess data = new DBAccess();
        int count = 0;
        //creates total list of the customers appointments
        ObservableList<LocalDateTime[]> customerAppTimes = data.customerAssociatedAppointments(
                data.getCustomerID(CustomerName));

        //loops through the appointments
        for (LocalDateTime[] customerAppTime : customerAppTimes) {
            //checks to see if there is an appointment within the same day
            if(start.getDayOfYear() == customerAppTime[1].getDayOfYear()) {
                //checks to make sure that the time being compared is not to itself as that would be an overlap that
                //is not needed to be protected against as it is about to be overwritten
                if((!(ZonedDateTime.of(customerAppTime[0], ZoneId.of("UTC")).equals(originalStart.withZoneSameInstant(ZoneId.of("UTC"))))) &&
                        (!(ZonedDateTime.of(customerAppTime[1],ZoneId.of("UTC")).equals(originalEnd.withZoneSameInstant(ZoneId.of("UTC")))))){
                    //checks to see if the appointment times are able to fit between other given values or over the values
                    //or there is another form of overlap whether that be the same time as another appointment etc.
                    if (((((start.withZoneSameInstant(ZoneId.of("UTC")).isBefore(ZonedDateTime.of(customerAppTime[1], ZoneId.of("UTC")))) &&
                            (start.withZoneSameInstant(ZoneId.of("UTC")).isAfter(ZonedDateTime.of(customerAppTime[0], ZoneId.of("UTC")))))
                            || ((end.withZoneSameInstant(ZoneId.of("UTC")).isAfter(ZonedDateTime.of(customerAppTime[0], ZoneId.of("UTC")))) &&
                            end.withZoneSameInstant(ZoneId.of("UTC")).isBefore(ZonedDateTime.of(customerAppTime[1], ZoneId.of("UTC"))))) ||
                            ((start.withZoneSameInstant(ZoneId.of("UTC")).toLocalTime() == customerAppTime[0].toLocalTime()) ||
                                    (end.withZoneSameInstant(ZoneId.of("UTC")).toLocalTime() == customerAppTime[1].toLocalTime()))) ||
                            (start.withZoneSameInstant(ZoneId.of("UTC")).isBefore(ZonedDateTime.of(customerAppTime[0], ZoneId.of("UTC"))) &&
                                    (end.withZoneSameInstant(ZoneId.of("UTC")).isAfter(ZonedDateTime.of(customerAppTime[1], ZoneId.of("UTC")))))) {
                        return false;
                    }
                }
            }
        }
        //goes through loop and no matches is able to return true, no overlap
        return true;
    }

    /**
     * window to display to user that the appointment times are improperly assigned
     * @return the scene to be displayed to the user as a pop up window
     */
    private Scene errorUserTimeSet() {
        //sets the language and the location to the user's computers default settings
        Locale user_local = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", user_local);

        //sets the error message for the user
        Label errorMessage = new Label(messages.getString("timeSelectErrorCode"));

        //sets the button for the user to be able to navigate through the application
        Button okButton = new Button(messages.getString("okLabel"));

        //sets the event handler for the button to be able to close the window
        EventHandler<ActionEvent> okEvent = e -> ((Stage)okButton.getScene().getWindow()).close();
        //sets the button to the event handler
        okButton.setOnAction(okEvent);

        //formats the window and returns it to the main form
        HBox format = new HBox(errorMessage, okButton);
        format.setSpacing(10);
        format.setPadding(new Insets(10, 10, 10, 10));
        return new Scene(format);
    }
}
