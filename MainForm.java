import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.ResourceBundle;

import static javafx.application.Application.launch;

/**
 * This is the main interactive form that the user will navigate through to get to all features of the application
 @author Lucas Hynes
 @version 1.0
 @since 11/18/2020
 */
public class MainForm{
    //user object to track login and appointment actions
    private User user;

    //sets the settings for language and country for the window
    Locale user_local = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
    ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", user_local);

    /**
     * Main function call to launch application in a new window for the user
     * @param args arguments to pass to main function
     */
    public static void main(String[] args) {
        //launches the start functions stage
        launch(args);
    }

    /**
     * Function to create layout and function for the main function window.
     *
     * Lambda used to simplify the process of getting the toggled properties from the radio buttons and being able
     *                      *  to reason through the user's selections.
     *
     *  Using a lambda expression to express the inner event handler of the pop up window to
     *                      *  be able to control the different events the lambda is used to effectively call the methods
     *                      *  needed to provide the required action.
     *
     * @param primaryStage the current active base stage
     * @throws SQLException protects from invalid user input
     */
    public void start(Stage primaryStage) throws SQLException {
        //sets the language and country to the users default computer settings
        Locale user_local = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", user_local);

        //sets the title for the application along with the dimensions of the window
        String titleStage = messages.getString("stageTitleLabel");
        primaryStage.setTitle(titleStage);
        primaryStage.setHeight(600);
        primaryStage.setWidth(1100);

        //sets the access to call methods to access and change the database
        DBAccess data = new DBAccess();

        //labels string values defined using resource bundle and key words
        String weekLabel = messages.getString("weekLabel");
        String monthLabel = messages.getString("monthLabel");

        //creates the radio button for the user to select their preferred view of the table and the range of dates of
        //appointments to be viewed by the user (default is no filter on the view and all appointments are shown
        RadioButton weeklyOutlook = new RadioButton(weekLabel);
        RadioButton monthlyOutlook = new RadioButton(monthLabel);

        //sets to group radio buttons and have the compatibility with user input requests
        ToggleGroup toggle = new ToggleGroup();
        weeklyOutlook.setToggleGroup(toggle);
        monthlyOutlook.setToggleGroup(toggle);

        //sets the table view for the appointments to be viewed by the user
        TableView appointmentView = new TableView();

        //sets the string values for the labels of the columns, dynamically assigned based on language settings
        String appointmentLabel = messages.getString("appointmentTableLabel");
        String titleLabel = messages.getString("titleTableLabel");
        String descriptionLabel = messages.getString("descriptionTableLabel");
        String locationLabel = messages.getString("locationTableLabel");
        String contactLabel = messages.getString("contactTableLabel");
        String typeLabel = messages.getString("typeTableLabel");
        String startLabel = messages.getString("startTableLabel");
        String endLabel = messages.getString("endTableLabel");
        String customerLabel = messages.getString("customerTableLabel");

        //initializes the column objects for the table, with the label applied to the columns
        TableColumn<Appointment, String> appointmentIdCol = new TableColumn<>(appointmentLabel);
        TableColumn<Appointment, String> titleCol = new TableColumn<>(titleLabel);
        TableColumn<Appointment, String> descriptionCol = new TableColumn<>(descriptionLabel);
        TableColumn<Appointment, String> locationCol = new TableColumn<>(locationLabel);
        TableColumn<Appointment, String> contactCol = new TableColumn<>(contactLabel);
        TableColumn<Appointment, String> typeCol = new TableColumn<>(typeLabel);
        TableColumn<Appointment, ZonedDateTime> startCol = new TableColumn<>(startLabel);
        TableColumn<Appointment, ZonedDateTime> endCol = new TableColumn<>(endLabel);
        TableColumn<Appointment, String> customerIdCol = new TableColumn<>(customerLabel);
        TableColumn<Appointment, String> userIdCol = new TableColumn<>(messages.getString("userTableLabel"));

        //sets the cell value property to connect to the correct properties of the various classes and variable types
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("Appointment_ID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("Description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("Location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("Contact_ID"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("Type"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("Start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("End"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("Customer_ID"));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("User_ID"));

        //adds all of the columns to the table
        appointmentView.getColumns().addAll(appointmentIdCol, titleCol, descriptionCol, locationCol,
                contactCol, typeCol, startCol, endCol, customerIdCol, userIdCol);
        //sets the items to a list retrieved by a database method call
        appointmentView.getItems().addAll(data.getAppointmentArray());
        //sets the size of the table for proper formatting
        appointmentView.setMinWidth(800);
        appointmentView.setMaxHeight(400);

        //sets the date picker for the user to be able to select the right week or month selection, based on which week
        //or month the user wants to be displayed on the table
        DatePicker selectViewTerm = new DatePicker();
        selectViewTerm.setValue(LocalDate.now());

        //creates the strings that the buttons have displayed on them, assigned based on user computer language settings
        String addCustomerLabel = messages.getString("addCustomerLabel");
        String addAppointmentLabel = messages.getString("addAppointmentLabel");
        String modifyCustomerLabel = messages.getString("modifyCustomerLabel");
        String modifyAppointmentLabel = messages.getString("modifyAppointmentLabel");
        String deleteCustomerLabel = messages.getString("deleteCustomerLabel");
        String deleteAppointmentLabel = messages.getString("deleteAppointmentLabel");
        String reportLabel = messages.getString("reportLabel");

        //declares the buttons with the strings called with the method
        Button addCustomer = new Button(addCustomerLabel);
        Button addAppointment = new Button(addAppointmentLabel);
        Button modifyCustomer = new Button(modifyCustomerLabel);
        Button modifyAppointment = new Button(modifyAppointmentLabel);
        Button deleteCustomer = new Button(deleteCustomerLabel);
        Button deleteAppointment = new Button(deleteAppointmentLabel);
        Button reportPage = new Button(reportLabel);

        //sets event listener for the radio button table view selection
        /**
         *  @Lambda used to simplify the process of getting the toggled properties from the radio buttons and being able
         *  to reason through the user's selections
         */
        toggle.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {

            //checks to see if the week radio option has been selected
            if (weeklyOutlook == toggle.getSelectedToggle()) {
                //gets the selected date from the user input of the date picker
                LocalDate dateComparison = selectViewTerm.getValue();
                //calculates the week number of the day relative to the year
                int weekNo = dateComparison.get(WeekFields.ISO.weekOfWeekBasedYear());
                try {
                    //removes values, then method call to get all appointments with the same week number value
                    appointmentView.getItems().clear();
                    appointmentView.getItems().removeAll();
                    appointmentView.getItems().addAll(appByWeek(weekNo));
                } catch (SQLException throwables) {
                    //catches any invalid user inputs
                    throwables.printStackTrace();
                }
            }
            //checks if the month radio button has been selected
            else if (monthlyOutlook == toggle.getSelectedToggle()) {
                //retrieves the date selected by the user
                LocalDate dateComparison = selectViewTerm.getValue();
                //gets the month value of the date selected
                int monthNo = dateComparison.getMonthValue();
                try {
                    //retrieves all/any appointments within the same month
                    appointmentView.getItems().clear();
                    appointmentView.getItems().removeAll();
                    appointmentView.getItems().addAll(appByMonth(monthNo));
                } catch (SQLException throwables) { throwables.printStackTrace(); }
            }
            //if there is no selection
            else {
                //removes any previously set data
                appointmentView.getItems().clear();
                appointmentView.getItems().removeAll();
                try {
                    //attempts to set the data to not have any filter
                    appointmentView.getItems().addAll(data.getAppointmentArray());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        //handles when the user selects a different date from the date picker
        EventHandler<ActionEvent> daySelectEvent = e -> {
            //gets the date from the date picker
            LocalDate temp = selectViewTerm.getValue();

            //removes items to fill with new values
            appointmentView.getItems().clear();
            appointmentView.getItems().removeAll();

            //checks if the user want to see the week or month value
            if(weeklyOutlook.isSelected()){
                try {
                    //adds all the appointments within the selected week
                    appointmentView.getItems().add(appByWeek(temp.get(WeekFields.ISO.weekOfWeekBasedYear())));
                } catch (SQLException throwables) {
                    //checks for invalid input
                    throwables.printStackTrace();
                }
            }
            //checks if month is selected by the user
            else if(monthlyOutlook.isSelected()) {
                try {
                    //adds all of the appointments to the table that are in the selected month
                    appointmentView.getItems().add(appByMonth(temp.getMonthValue()));
                } catch (SQLException throwables) {
                    //checks for invalid inputs
                    throwables.printStackTrace();
                }
            }
            //if nothing selected clears the table
            else {
                try {
                    //adds all values regardless of date
                    appointmentView.setItems(data.getAppointmentArray());
                } catch (SQLException throwables) {
                    //catches any invalid inputs
                    throwables.printStackTrace();
                }
            }
        };
        //applies the button to the event handler
        selectViewTerm.setOnAction(daySelectEvent);

        //handles when the user clicks the add customer button
        EventHandler<ActionEvent> addCustomerEvent = e -> {
            //creates a new instance of a add customer scene
            AddCustomerForm newCustomer = new AddCustomerForm();
            try {
                //creates a second stage for the scene to appear on
                Stage secondStage = new Stage();
                //sets the scene to the main method call of the add customer class to display scene
                secondStage.setScene(newCustomer.addCustomer());
                secondStage.show();
            } catch (SQLException throwables) {
                //catches any invalid inputs
                throwables.printStackTrace();
            }
        };
        //applies the button with th event handler
        addCustomer.setOnAction(addCustomerEvent);

        //handles when the user clicks the modify customer button
        EventHandler<ActionEvent> modifyCustomerEvent = e -> {
            //launches a window to be able to select which customer to modify
            Stage customerSelect = new Stage();
            try {
                //launches the scene based on classes private method call
                customerSelect.setScene(customerSelectModify());
            } catch (SQLException throwables) {
                //catches any invalid inputs
                throwables.printStackTrace();
            }
            //shows the window
            customerSelect.show();
        };
        //applies the button to the event
        modifyCustomer.setOnAction(modifyCustomerEvent);

        //handles when the user clicks the delete customer button
        EventHandler<ActionEvent> deleteCustomerEvent = e -> {
            try {
                //launches private class method to select which customer to delete
                Stage secondStage = new Stage();
                //displays the window for the user to be able to delete a customer
                secondStage.setScene(customerSelectDelete());
                secondStage.show();
            } catch (SQLException throwables) {
                //catches any invalid inputs
                throwables.printStackTrace();
            }
        };
        //applies the button to the event
        deleteCustomer.setOnAction(deleteCustomerEvent);

        //handles when the user clicks the add appointment button
        EventHandler<ActionEvent> addAppointmentEvent = e -> {
            //creates a class instance for the new appointment window
            AddAppointmentForm newAppointment = new AddAppointmentForm();

            //creates new stage for the adding appointment
            Stage secondStage = new Stage();
            try {
                //adds the scene with the method from the new class instance
                secondStage.setScene(newAppointment.addAppointment(getUser()));
                //shows the window
                secondStage.show();
            } catch (SQLException throwables) {
                //catches any invalid inputs
                throwables.printStackTrace();
            }

            //gets the main window and closes it so when the new appointment is added it is updated in the main form
            Stage stage = (Stage) addAppointment.getScene().getWindow();
            stage.close();
        };
        //adds the button to the event
        addAppointment.setOnAction(addAppointmentEvent);

        //handles when thr user clicks the modify appointment button
        EventHandler<ActionEvent> modifyAppointmentEvent = e -> {
            //creates new class instance for the modify appointment form
            ModifyAppointmentForm modAppointment = new ModifyAppointmentForm();

            //creates new stage for the modify appointment window
            Stage secondStage = new Stage();

            try {
                if(appointmentView.getSelectionModel().getSelectedItem() != null) {
                    //sets the scene to the class instance with the user selected appointment from the table
                    secondStage.setScene(modAppointment.modifyAppointment(
                            (Appointment) appointmentView.getSelectionModel().getSelectedItem(), user));
                    //displays the window
                    secondStage.show();


                    //closes the main stage so that when the stage is opened again
                    Stage stage = (Stage) modifyAppointment.getScene().getWindow();
                    stage.close();
                }
                //goes if there was no selection of the main form's table
                else {
                    //settings to display to the user the error pop up window
                    Stage errorDisplay = new Stage();
                    errorDisplay.setScene(noSelectionError());
                    errorDisplay.show();
                }
            } catch (SQLException throwables) {
                //catches any invalid inputs
                throwables.printStackTrace();
            }
        };
        //adds the button to the event
        modifyAppointment.setOnAction(modifyAppointmentEvent);

        //handles when the user selects the delete appointment button
        EventHandler<ActionEvent> deleteAppointmentEvent = e -> {
            try {
                if(appointmentView.getSelectionModel().getSelectedItem() != null) {
                    //creates a new stage for the delete confirmation
                    Stage stageConfirmDelete = new Stage();

                    //creates a new label where it displays the appointment id and the title of the delete
                    Label confirmDelete = new Label(
                            ((Appointment) appointmentView.getSelectionModel().getSelectedItem()).getAppointment_ID() +
                                    " : " + ((Appointment) appointmentView.getSelectionModel().getSelectedItem()).getTitle() +
                                    " -> " + messages.getString("confirmDelete"));

                    //creates a new ok button for the user to navigate through the application
                    Button okButton = new Button(messages.getString("okLabel"));

                    //adds the event for the ok button to close the new window
                    /**
                     *  @Lambda Using a lambda expression to express the inner event handler of the pop up window to
                     *  be able to control the different events the lambda is used to effectively call the methods
                     *  needed to provide the required action
                     */
                    EventHandler<ActionEvent> okEvent = e1 -> ((Stage) okButton.getScene().getWindow()).close();
                    //adds the button to the event
                    okButton.setOnAction(okEvent);

                    //sets the layout for the display window
                    VBox format = new VBox(confirmDelete, okButton);
                    //settings for the window
                    format.setSpacing(20);
                    format.setPadding(new Insets(10, 10, 10, 10));
                    //shows the scene
                    Scene deleteConfirm = new Scene(format);
                    stageConfirmDelete.setScene(deleteConfirm);
                    //shows the final result window
                    stageConfirmDelete.show();

                    //calls the delete appointment method from the database access based on the selected appointment's id
                    data.deleteAppointment((
                            (Appointment) appointmentView.getSelectionModel().getSelectedItem()).getAppointment_ID());

                    //removes all values
                    appointmentView.getItems().clear();
                    appointmentView.getItems().removeAll();
                    //gets the new data array and sets it to the table
                    appointmentView.getItems().addAll(data.getAppointmentArray());
                }
                //goes if there was no selection of the main form's table
                else {
                    //settings to display to the user the error pop up window
                    Stage errorDisplay = new Stage();
                    errorDisplay.setScene(noSelectionError());
                    errorDisplay.show();
                }
            } catch (SQLException throwables) {
                //catches any invalid input
                throwables.printStackTrace();
            }
        };
        //adds the button to the event
        deleteAppointment.setOnAction(deleteAppointmentEvent);

        //handles when the user selects the report page button
        EventHandler<ActionEvent> reportPageEvent = e -> {
            //creates new stage to display the report information
            Stage reportStage = new Stage();

            //creates new class instance for the display window of the reports
            ReportClass report = new ReportClass();

            try {
                //sets the scene
                reportStage.setScene(report.reportScreen());
            } catch (SQLException throwables) {
                //catches any invalid inputs
                throwables.printStackTrace();
            }
            //shows the window
            reportStage.show();
        };
        //adds the button to the event
        reportPage.setOnAction(reportPageEvent);

        //sets the layout for the two radio buttons
        HBox radioButtonGroup = new HBox(weeklyOutlook, monthlyOutlook);
        //sets alignment and spacing for the radio buttons
        radioButtonGroup.setAlignment(Pos.CENTER);
        radioButtonGroup.setSpacing(10);

        //sets the layout for the date selection section of the main form
        VBox dateSelectionGroup = new VBox(radioButtonGroup, selectViewTerm);
        //sets layout and spacing for the date select section
        dateSelectionGroup.setAlignment(Pos.CENTER);
        dateSelectionGroup.setSpacing(10);

        //sets the layout for the customer modification group
        VBox customerModButtons = new VBox(addCustomer, modifyCustomer, deleteCustomer);
        //sets layout and spacing for the customer mod button groups
        customerModButtons.setSpacing(10);
        customerModButtons.setAlignment(Pos.CENTER);

        //sets the layout for the appointment modification group
        VBox appointmentModButtons = new VBox(addAppointment, modifyAppointment, deleteAppointment);
        //sets layout and spacing for the appointment mod button groups
        appointmentModButtons.setSpacing(10);
        appointmentModButtons.setAlignment(Pos.CENTER);

        //sets the layout for all the different input selection groups
        VBox buttonsGroup = new VBox(customerModButtons, appointmentModButtons, reportPage);
        //sets the layout and spacing for the different input selection groups
        buttonsGroup.setSpacing(20);
        buttonsGroup.setAlignment(Pos.CENTER);

        //sets the layout for the right side of the scene, which is the date picker and the buttons group
        VBox rightSide = new VBox(dateSelectionGroup, buttonsGroup);
        //sets the layout and spacing for the right side of the window
        rightSide.setSpacing(40);
        rightSide.setAlignment(Pos.CENTER);

        //sets the left side of the window to bw the appointment view table
        VBox leftSide = new VBox(appointmentView);
        //sets the spacing and layout of the left side of the window
        leftSide.setAlignment(Pos.CENTER);
        leftSide.setPadding(new Insets(0, 0, 0,50));

        //sets the left and right side of the window together
        HBox finalGroup = new HBox(leftSide, rightSide);
        finalGroup.setSpacing(50);

        //sets the scene for the main form to the final group
        Scene scene = new Scene(finalGroup);
        //sets the primary stage to the scene just declared
        primaryStage.setScene(scene);
        primaryStage.show();

        //calls the pop up for the user about any upcoming appointment within 15 minutes of signing in
        Stage secondary = new Stage();
        secondary.setScene(appointmentUpcomingCheck());
        secondary.initOwner(primaryStage);
        secondary.initModality(Modality.APPLICATION_MODAL);
        secondary.show();
    }

    /**
     * Returns any appointments that have the same month value as the value passed into the method
     * @param monthNo the month number (1-12) of the appointments the user wants to view
     * @return the observable list of the appointments by the month
     * @throws SQLException for any value that is invalid, such as too large of the range of months
     */
    private ObservableList<Appointment> appByMonth(int monthNo) throws SQLException {
        //sets access to database methods
        DBAccess data = new DBAccess();

        //declares the list of appointment within the month
        ObservableList<Appointment> byMonthApp = FXCollections.observableArrayList();
        //gets all available appointments from the database
        ObservableList<Appointment> appList = data.getAppointmentArray();

        //loops through all of the appointments
        for (Appointment temp : appList) {
            //gets a temp value
            //if the appointment has the same month value as the given value
            if (temp.getStart().getMonthValue() == monthNo) {
                //adds the appointment to the list to return to the user
                byMonthApp.add(temp);
            }
        }
        //returns all matches of the month value
        if(byMonthApp.isEmpty()) { return null; }
        else { return byMonthApp; }
    }

    /**
     * Returns any appointments that have the same week value as the value passed into the method
     * @param weekNo passes the week number to find the matches to any other appointments
     * @return returns the list of appointment that have the same month value
     * @throws SQLException for any value passed in that is invalid and throws an error
     */
    private ObservableList<Appointment> appByWeek(int weekNo) throws SQLException {
        //allows access to database method calls
        DBAccess data = new DBAccess();

        //initializes list of appointments with a matching week value
        ObservableList<Appointment> byWeekApp = FXCollections.observableArrayList();
        //initializes the list of all appointments in the database
        ObservableList<Appointment> appList = data.getAppointmentArray();
        //loops through thr list of the appointments
        for (Appointment temp : appList) {
            //if the week matches add to the list
            if (temp.getStart().get(WeekFields.ISO.weekOfWeekBasedYear()) == weekNo) {
                byWeekApp.add(temp);
            }
        }
        //returns the appointments within the same week value
        if(byWeekApp.isEmpty()) { return null; }
        else { return byWeekApp; }
    }

    /**
     * launches a window for the user to select which customer to modify
     * @return returns the window where the user selects which customer to modify
     * @throws SQLException throws exception for any invalid inputs
     */
    private Scene customerSelectModify() throws SQLException {
        //sets the language and country settings of the form based on the users computer settings
        Locale user_local = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", user_local);

        //allows access to database method calls
        DBAccess data = new DBAccess();

        //gets the array of the customers from the database
        ObservableList<Customer> customers = data.getCustomerArray();

        //creates new table view of all database customers
        TableView customerTable = new TableView();

        //initializes the customer information columns
        TableColumn<Customer, String> customerIdCol = new TableColumn<>(messages.getString("customerTableLabel"));
        TableColumn<Customer, String> customerNameCol = new TableColumn<>(messages.getString("customerNameTableLabel"));
        TableColumn<Customer, String> customerAddressCol = new TableColumn<>(messages.getString("addressTableLabel"));
        TableColumn<Customer, String> customerPostalCodeCol = new TableColumn<>(messages.getString("zipTableLabel"));
        TableColumn<Customer, String> customerPhoneCol = new TableColumn<>(messages.getString("phoneTableLabel"));
        TableColumn<Customer, String> divisionCol = new TableColumn<>(messages.getString("divisionTableLabel"));

        //sets the cell value factory methods to the proper class variables
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("Customer_ID"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("Customer_Name"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("Address"));
        customerPostalCodeCol.setCellValueFactory(new PropertyValueFactory<>("Postal_Code"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("Phone"));
        divisionCol.setCellValueFactory(new PropertyValueFactory<>("Division_ID"));

        //adds all the columns to the table view
        customerTable.getColumns().addAll(customerIdCol, customerNameCol, customerAddressCol, customerPostalCodeCol,
                customerPhoneCol, divisionCol);
        //sets the values to all the customers
        customerTable.setItems(customers);

        //creates select button for user to navigate through
        Button selectButton = new Button(messages.getString("selectLabel"));

        //creates event handler for the selection button
        EventHandler<ActionEvent> selectButtonEvent = e -> {

            //checks to see if the user has selected anything from the table
            if(customerTable.getSelectionModel().getSelectedItem() == null) {
                //if no selection, return error to user
                Stage errorDisplay = new Stage();
                errorDisplay.setScene(noSelectionError());
                errorDisplay.show();
            }
            //if selected continue
            else {

                //declares the selected customer to the users selection based on the table
                Customer selectedCustomer = (Customer) customerTable.getSelectionModel().getSelectedItem();

                //closes the customer selection window
                Stage temp = ((Stage) selectButton.getScene().getWindow());
                temp.close();

                //creates a new class instance to launch a modify customer form
                ModifyCustomerForm modCustomerForm = new ModifyCustomerForm();
                try {
                    //sets the scene to be based on the selected customer the user selected
                    Scene modCustomerScene = modCustomerForm.modifyCustomer(selectedCustomer);
                    //creates new stage for the window
                    Stage modCustomer = new Stage();
                    //sets the scene and shows to the user
                    modCustomer.setScene(modCustomerScene);
                    modCustomer.show();
                } catch (SQLException throwables) {
                    //catches any invalid inputs from the user
                    throwables.printStackTrace();
                }
            }
        };
        //adds the button to the event handler
        selectButton.setOnAction(selectButtonEvent);

        //sets the layout for the selection modify window
        VBox combo = new VBox(customerTable, selectButton);
        combo.setSpacing(20);
        combo.setPadding(new Insets(10, 10, 10, 10));

        //returns the scene to the main form to be controlled by thr buttons
        return new Scene(combo);
    }

    /**
     * returns the scene for the user to be able to delete a customer from the database, after clearance for the
     * deletion is accepted
     * @return scene for the user to be able to remove customers
     * @throws SQLException throws error for input error from user
     */
    private Scene customerSelectDelete() throws SQLException {
        //provides method calls to the database
        DBAccess data = new DBAccess();

        //list of customers from the database
        ObservableList<Customer> customers = data.getCustomerArray();

        //creates the table view of the customers
        TableView customerTable = new TableView();

        //initializes the customer information columns
        TableColumn<Customer, String> customerIdCol = new TableColumn<>(messages.getString("customerTableLabel"));
        TableColumn<Customer, String> customerNameCol = new TableColumn<>(messages.getString("customerNameTableLabel"));
        TableColumn<Customer, String> customerAddressCol = new TableColumn<>(messages.getString("addressTableLabel"));
        TableColumn<Customer, String> customerPostalCodeCol = new TableColumn<>(messages.getString("zipTableLabel"));
        TableColumn<Customer, String> customerPhoneCol = new TableColumn<>(messages.getString("phoneTableLabel"));
        TableColumn<Customer, String> divisionCol = new TableColumn<>(messages.getString("divisionTableLabel"));

        //sets the cell value factory methods to the proper class variables
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("Customer_ID"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("Customer_Name"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("Address"));
        customerPostalCodeCol.setCellValueFactory(new PropertyValueFactory<>("Postal_Code"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("Phone"));
        divisionCol.setCellValueFactory(new PropertyValueFactory<>("Division_ID"));

        //adds all of the defined columns to the table
        customerTable.getColumns().addAll(customerIdCol, customerNameCol, customerAddressCol, customerPostalCodeCol,
                customerPhoneCol, divisionCol);
        //adds the customer data to the table
        customerTable.setItems(customers);

        //sets the button for the user to navigate through the application
        Button selectButton = new Button(messages.getString("selectLabel"));

        //sets the event for the button functionality
        EventHandler<ActionEvent> selectButtonEvent = e -> {

            //attempts to delete the selected customer
            try {

                //checks to see if the user has selected anything from the table
                if(customerTable.getSelectionModel().getSelectedItem() == null) {
                    //if no selection, return error to user
                    Stage errorDisplay = new Stage();
                    errorDisplay.setScene(noSelectionError());
                    errorDisplay.show();
                }
                //if selected continue
                else {
                    Customer selectedCustomer = (Customer) customerTable.getSelectionModel().getSelectedItem();

                    if (data.deleteCustomer(selectedCustomer.getCustomer_ID())) {

                        //creates stage for the user confirmation of the customer deletion
                        Stage stageConfirmDelete = new Stage();
                        //displays the deleted customers information
                        Label confirmDelete = new Label(
                                ((Customer) customerTable.getSelectionModel().getSelectedItem()).getCustomer_Name() +
                                        " " + messages.getString("confirmDelete"));

                        //is the button for the user to navigate through the application
                        Button okButton = new Button(messages.getString("okLabel"));

                        //adds the event handler for the button to close the window
                        EventHandler<ActionEvent> okEvent = e1 -> ((Stage) okButton.getScene().getWindow()).close();
                        //applies the button to the event
                        okButton.setOnAction(okEvent);

                        //sets the layout of the window
                        VBox format = new VBox(confirmDelete, okButton);
                        //sets the layout settings for the window
                        format.setSpacing(10);
                        format.setPadding(new Insets(10, 10, 10, 10));

                        //sets the scene for the deletion confirmation
                        Scene deleteConfirm = new Scene(format);
                        stageConfirmDelete.setScene(deleteConfirm);
                        stageConfirmDelete.show();


                        //closes the window
                        Stage temp = ((Stage) selectButton.getScene().getWindow());
                        temp.close();
                    } else {
                        //label to tell the user the deletion was unsuccessful and closes the window
                        Label errorLabel = new Label(messages.getString("deleteRestrict"));
                        //button to allow user to navigate
                        Button okButton = new Button(messages.getString("okLabel"));

                        //sets event to close the pop up
                        EventHandler<ActionEvent> okEvent = e1 -> ((Stage) okButton.getScene().getWindow()).close();
                        okButton.setOnAction(okEvent);

                        //sets the layout for the window
                        VBox errorLayout = new VBox(errorLabel, okButton);
                        errorLayout.setPadding(new Insets(10, 10, 10, 10));
                        errorLayout.setSpacing(10);

                        //sets the window for the user to view
                        Stage stage = new Stage();
                        Scene scene = new Scene(errorLayout);
                        stage.setScene(scene);
                        stage.show();

                        //closes the deletion window as well
                        ((Stage) selectButton.getScene().getWindow()).close();
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };
        //applies the button to the event
        selectButton.setOnAction(selectButtonEvent);

        //sets the layout
        VBox combo = new VBox(customerTable, selectButton);
        //sets the layout settings
        combo.setSpacing(20);
        combo.setPadding(new Insets(10, 10, 10, 10));

        //returns the scene
        return new Scene(combo);
    }

    /**
     * checks to see if there is an upcoming appointment within 15 minutes of the users login time and sets an alert
     * for the user in a pop up window
     * @return the scene for the pop up warning window
     * @throws SQLException throws exception on invalid user input
     */
    private Scene appointmentUpcomingCheck() throws SQLException {
        //sets the location and language of the pop up base don the default system settings
        Locale user_local = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", user_local);

        //allows access to database method calls
        DBAccess data = new DBAccess();

        //a list of all database appointments
        ObservableList<Appointment> appointmentList = data.getAppointmentArray();

        //object to be assigned to the closet appointment within the 15 minute window
        Appointment targetToWarn = null;

        //loops through all the appointments
        for (Appointment appointment : appointmentList) {
            //checks to see if the appointment being looped through is within the target 15 minute zone
            if ((LocalDateTime.now().plus(15, ChronoUnit.MINUTES).isAfter(
                    appointment.getStart().toLocalDateTime())) && (LocalDateTime.now().isBefore(
                    appointment.getStart().toLocalDateTime()))) {
                //if an appointment is found within the allotted time return the appointment
                targetToWarn = appointment;
                //escapes the loop
                break;
            }
        }

        //if there is an appointment happening in the next 15
        if(targetToWarn != null) {
            //creates the label to tell the user the appointment is coming up
            Label warning = new Label(messages.getString("upComingMessage"));
            //creates labels for the appointment id and the appointment start time
            Label appointmentID = new Label(messages.getString("appointmentTableLabel"));
            Label startDateTime = new Label(messages.getString("startTableLabel"));
            //sets the labels for the upcoming appointment information
            Label appointmentIDField = new Label(String.valueOf(targetToWarn.getAppointment_ID()));
            Label startDateTimeField = new Label(targetToWarn.getStart().toString());

            //creates the grid pane layout for the information of the upcoming appointment
            GridPane layout = new GridPane();
            //adds the elements to the grid pane
            layout.add(appointmentID, 0, 0, 1, 1);
            layout.add(appointmentIDField, 0, 1, 1, 1);
            layout.add(startDateTime, 1, 0, 1, 1);
            layout.add(startDateTimeField, 1, 1, 1, 1);
            //sets the spacing for the grid pane
            layout.setVgap(10);
            layout.setHgap(20);

            //sets the button for the user to navigate through the program
            Button okButton = new Button(messages.getString("okLabel"));

            //sets the event handler of the button
            EventHandler<ActionEvent> okEvent = e1 -> ((Stage) okButton.getScene().getWindow()).close();
            //adds the button to the event handler
            okButton.setOnAction(okEvent);

            //sets the layout for the window
            VBox finalLayout = new VBox(warning, layout, okButton);
            finalLayout.setSpacing(20);

            //returns the scene to the main form
            return new Scene(finalLayout);
        }
        //there are no upcoming appointments within the given time span
        else {
            //displays a label with the message of no upcoming appointments
            Label noUpcoming = new Label(messages.getString("noUpcoming"));

            //sets the button for the user to navigate through the application
            Button okButton = new Button(messages.getString("okLabel"));

            //adds the event handler for the button
            EventHandler<ActionEvent> okEvent = e1 -> ((Stage) okButton.getScene().getWindow()).close();
            //adds the button to the event handler
            okButton.setOnAction(okEvent);

            //sets the layout for the scene
            VBox layout = new VBox(noUpcoming, okButton);
            layout.setSpacing(10);
            layout.setPadding(new Insets(10, 10, 10, 10));

            //returns the scene to the main form
            return new Scene(layout);
        }
    }

    /**
     * returns the active application user
     * @return active user
     */
    public User getUser() {
        return user;
    }

    /**
     * sets the active user for the application
     * @param user the active user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Displays to the user that they had not selected an item from any given table as this error is treated as generic
     * @return the scene to display to the user the information and allow navigation
     */
    private Scene noSelectionError(){
        //creates the elements of the pop up
        Label noSelect = new Label(messages.getString("noSelectError"));
        Button ok = new Button(messages.getString("okLabel"));

        //handles closing the window
        EventHandler<ActionEvent> okEvent = e -> ((Stage)ok.getScene().getWindow()).close();
        ok.setOnAction(okEvent);

        //handles layout for the pop up
        VBox layout = new VBox(noSelect, ok);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setSpacing(10);

        //returns the scene for the user displayed
        return new Scene(layout);
    }
}
