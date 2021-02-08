import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Add customer to the database to have more customer options for the appointments to then be created
 @author Lucas Hynes
 @version 1.0
 @since 11/20/2020
 */
public class AddCustomerForm {

    /**
     * returns the format of the add customer form with input handles to be able to create customers based on the
     * input fields completed by the user
     * @return the Form that allows users to add customers
     * @throws SQLException catches any improper input from the user
     */
    public Scene addCustomer() throws SQLException {
        //sets the country and language based on the settings of the user's computer
        Locale user_local = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", user_local);

        //initializes access to database method calls
        DBAccess data = new DBAccess();

        //setting layout for customer id label
        Label customerIdLabel = new Label(messages.getString("customerIDLabel"));
        customerIdLabel.setPadding(new Insets(5,5,5,5));

        //setting layout for name label
        Label nameLabel = new Label(messages.getString("nameLabel"));
        nameLabel.setPadding(new Insets(5,5,5,5));

        //setting for address label
        Label addressLabel = new Label(messages.getString("addressLabel"));
        addressLabel.setPadding(new Insets(5,5,5,5));

        //setting for postal code label
        Label postalCodeLabel = new Label(messages.getString("zipCodeLabel"));
        postalCodeLabel.setPadding(new Insets(5,5,5,5));

        //setting for phone number label
        Label phoneNumberLabel = new Label(messages.getString("phoneNumberLabel"));
        phoneNumberLabel.setPadding(new Insets(5,5,5,5));

        //setting for country label
        Label countryLabel = new Label(messages.getString("countryLabel"));
        countryLabel.setPadding(new Insets(5,5,5,5));

        //setting for division label
        Label divisionLabel = new Label(messages.getString("divisionLabel"));
        divisionLabel.setPadding(new Insets(5,5,5,5));

        //setting for the text field displayed as not being able to edit, is automatically set after submission
        //requirements met to the next available index
        TextField customerIdField = new TextField(messages.getString("idEditDisabled"));
        customerIdField.setEditable(false);

        TextField nameField = new TextField();
        TextField addressField = new TextField();
        TextField postalCodeField = new TextField();
        TextField phoneNumberField = new TextField();

        ComboBox countryField = new ComboBox(data.getLocationArray());
        countryField.getSelectionModel().selectFirst();

        ComboBox divisionField = new ComboBox(data.getDivisionArray(countryField.getValue().toString()));

        Button saveButton = new Button(messages.getString("saveLabel"));

        Button cancelButton = new Button(messages.getString("cancelLabel"));

        EventHandler<ActionEvent> countryFieldEvent = e -> {
            if(!divisionField.getItems().isEmpty()){
                divisionField.getItems().removeAll();
            }

            divisionField.getItems().clear();

            try {
                divisionField.getItems().addAll(data.getDivisionArray(countryField.getValue().toString()));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            //TODO language and also for the label to change to the local term
        };
        // Set on action
        countryField.setOnAction(countryFieldEvent);

        //Event Handler for the Save Button
        EventHandler<ActionEvent> saveEvent = e -> {
            if(!divisionField.getItems().isEmpty()) {
                try {
                    data.addCustomer(nameField.getText(), addressField.getText(),
                            postalCodeField.getText(), phoneNumberField.getText(),
                            data.getDivisionID(divisionField.getValue()));
                    Stage stage = (Stage)saveButton.getScene().getWindow();
                    stage.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

        };
        // Set on action
        saveButton.setOnAction(saveEvent);

        EventHandler<ActionEvent> cancelEvent = e -> {
            Stage stage = ((Stage)cancelButton.getScene().getWindow());
            stage.close();
        };
        //Set on action
        cancelButton.setOnAction(cancelEvent);

        GridPane userInputLayout = new GridPane();
        userInputLayout.add(customerIdLabel, 0, 0, 1, 1);
        userInputLayout.add(customerIdField, 1, 0, 1, 1);
        userInputLayout.add(nameLabel, 0, 1, 1, 1);
        userInputLayout.add(nameField, 1, 1, 1, 1);
        userInputLayout.add(addressLabel, 0, 2, 1, 1);
        userInputLayout.add(addressField, 1, 2, 1, 1);
        userInputLayout.add(postalCodeLabel, 0, 3, 1, 1);
        userInputLayout.add(postalCodeField, 1, 3, 1, 1);
        userInputLayout.add(phoneNumberLabel, 0, 4, 1, 1);
        userInputLayout.add(phoneNumberField, 1, 4, 1, 1);
        userInputLayout.add(countryLabel, 0, 5, 1, 1);
        userInputLayout.add(countryField, 1, 5, 1, 1);
        userInputLayout.add(divisionLabel, 0, 6, 1, 1);
        userInputLayout.add(divisionField, 1, 6, 1, 1);
        userInputLayout.setHgap(20);
        userInputLayout.setVgap(20);

        HBox buttons = new HBox(saveButton, cancelButton);
        buttons.setPadding(new Insets(10,10,10,10));

        VBox finalLayout = new VBox(userInputLayout, buttons);
        finalLayout.setPadding(new Insets(10,10,10,10));

        Scene addCScene = new Scene(finalLayout);
        return addCScene;
    }
}
