import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * This is the class that is handling the access to the database through method calls from the user to ensure the
 * proper format, access, insert, update, and delete rules are able to be preserved
 */
public class DBAccess {

    //holds the information that allows the class to access the database
    private static final String userName = "U07bdJ";
    private static final String password = "53688980572";
    private static final String url = "jdbc:mysql://wgudb.ucertify.com:3306/WJ07bdJ";

    /**
     * gets the location array for the user
     * @return a list of the different locations
     * @throws SQLException handles any invalid saved countries
     */
    public ObservableList getLocationArray() throws SQLException {
        //defines the list to hold the different values
        ObservableList list = FXCollections.observableArrayList();

        //attempts the connection to the server, calling a SQL statement to retrieve the whole 'countries' table
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM countries");
             ResultSet rs = ps.executeQuery()) {
            //loops through the queries resulting set and adds all the country values
            while(rs.next()) { list.add(rs.getString("Country")); }
        }

        //returns the completed list of the different countries
        return list;
    }

    /**
     * returns the divisions that match the country based on the country selection
     * @param countryString the string of the country to have the array of divisions be based off of
     * @return the list of the divisions for the selected country
     * @throws SQLException for invalid country string value
     */
    public ObservableList getDivisionArray(String countryString) throws SQLException {
        //defines the list of divisions to hold
        ObservableList list = FXCollections.observableArrayList();

        //attempts to connect to the database and query for a result based on a sub-query to find the proper country
        //id and applies that to the parent query to find the divisions isolated by the country
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM first_level_divisions" +
                     " WHERE COUNTRY_ID = (" +
                     "SELECT Country_ID FROM countries WHERE Country = \"" + countryString + "\")" );
             ResultSet rs = ps.executeQuery()) {
            //loops through the results and adds all of the division values to the list
            while(rs.next()) { list.add(rs.getString("Division")); }
        }

        //returns the list to the user
        return list;
    }

    /**
     * gets the time zone of the user based on the default time settings of the user's computer
     * @return the zoned date time of the user's default settings
     */
    public ZonedDateTime getZonedTime(){
        return LocalDateTime.now().atZone(ZoneId.systemDefault());
    }

    /**
     * adds a customer to the database based on the values passed into the method
     * @param cName holds the new customer's name
     * @param cAddress holds the new customer's address
     * @param pC holds the new customer's postal code
     * @param phone holds the new customer's phone number
     * @param divisionId holds the new customer's division id
     * @return a boolean value based on the status of the creation
     * @throws SQLException for any invalid user input
     */
    public boolean addCustomer(String cName, String cAddress, String pC, String phone,int divisionId)
            throws SQLException {
        //attempts to connect to the database, then call a statement that creates a new entry into the customers
        //table based on the values passed in through the method
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO customers(Customer_Name," +
                     " Address, Postal_Code, Phone, Created_By, Last_Updated_By, Division_ID) " +
                     "VALUES(\"" + cName + "\", \"" + cAddress + "\", \"" + pC + "\", \"" + phone + "\", " +
                     "NULL, NULL, \"" +  divisionId + "\");" );
             ) {
            //returns the success or failure of the statement
            return ps.execute(); }
    }

    /**
     * allows the customer to modify a customer's information based on the user's constant value (user id)
     * @param cID the current and permanent id for the user
     * @param cName the newly edited name, or the unchanged value passed back through
     * @param cAddress the newly edited address, or the unchanged value passed back through
     * @param pC the newly edited postal code, or the unchanged value passed back through
     * @param phone the newly edited phone number, or the unchanged value passed back through
     * @param divisionId the newly edited division id, or the unchanged value passed back through
     * @return boolean value based on the status of the update
     * @throws SQLException throws error catches any invalid input
     */
    public boolean modifyCustomer(int cID, String cName, String cAddress, String pC, String phone,int divisionId)
            throws SQLException {
        //attempts to connect to the database, and sends a request to the server to update the values of the user based
        //on the user's id to the given values
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("UPDATE customers " +
                             "SET Customer_Name = \"" + cName + "\", Address = \"" + cAddress + "\", Postal_Code = \"" +
                             pC + "\", Phone = \"" + phone + "\", Division_ID = \"" + divisionId + "\"" +
                             "WHERE Customer_ID = \"" + cID + "\";")
        ) {
            //returns the status of the query
            return ps.execute();
        }
    }

    /**
     * allows the user to return the division id based on the division string value
     * @param DivisionString the division string to find the division id
     * @return the division id
     * @throws SQLException any invalid division strings
     */
    public int getDivisionID(Object DivisionString) throws SQLException {
        //attempts to connect to the database and returns the proper division id based on the string
        try (Connection conn = DriverManager.getConnection(url, userName, password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM first_level_divisions WHERE " +
                     "Division = \"" + DivisionString + "\";");
             ResultSet rs = ps.executeQuery()) {
            //loops through the resulting table
            while(rs.next()) {
                //returns the value of the row
                return Integer.parseInt(rs.getString("Division_ID"));
            }
        }
        //returns default not found value if could not be found
        return -1;
    }

    /**
     * checks to see if the user's login was successful by matching the username and password to the combination
     * stored within the database
     * @param username the user's username value
     * @param pWord the user's password (plain text)
     * @return the user's id if successful login, -1 if unsuccessful
     * @throws SQLException handles any invalid user input
     */
     public int checkLogin(String username, String pWord) throws SQLException {
         //attempts to connect to the database, then run a query to pull any users with a matching username
         try (Connection conn = DriverManager.getConnection(url, userName, password);
              PreparedStatement ps = conn.prepareStatement("SELECT * FROM users " +
                     "WHERE User_Name = \"" + username + "\";");
              ResultSet rs = ps.executeQuery()) {
             //loops through the results
             while(rs.next()){
                 //retrieves the password and checks to see if it is an equal value
                 if(rs.getString("Password").equals(pWord)){
                     //returns the user id of the user whose password matches
                     return Integer.parseInt(rs.getString("User_ID"));
                }
            }
         }
         //returns -1 if no password match or user name was found
         return -1;
    }

    /**
     * returns the array of customers from the database
     * @return the list of customers saved as class objects
     * @throws SQLException handles invalid user input
     */
    public ObservableList<Customer> getCustomerArray() throws SQLException {
        //list to hold the resulting customer set list
        ObservableList<Customer> list = FXCollections.observableArrayList();

        //attempts to connect to the database and retrieves the entire customer table
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers");
             ResultSet rs = ps.executeQuery()) {

            //loops through the result
            while(rs.next()){
                //adds the values from the table into the customer class object
                Customer newCustomer = new Customer(rs.getInt("Customer_ID"),
                        rs.getString("Customer_Name"), rs.getString("Address"),
                        rs.getString("Postal_Code"), rs.getString("Phone"),
                        rs.getInt("Division_ID"));
                //saves the new object to the list to be used by the user
                list.add(newCustomer);
            }
        }

        //returns the resulting list
        return list;
    }

    /**
     * retrieves the appointments saved within the database
     * @return list of class objects of the appointments
     * @throws SQLException handles any invalid input or table data
     */
    public ObservableList<Appointment> getAppointmentArray() throws SQLException {
        //creates a list to hold the appointments
        ObservableList<Appointment> list = FXCollections.observableArrayList();

        //attempts to connect to the database and retrieves the appointments table
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments");
             ResultSet rs = ps.executeQuery()) {

            //loops through the result
            while(rs.next()) {
                //then converts the times in format to the zone at which it was made after changing the value from a
                // timestamp style to the proper zone id of the user's computer
                ZonedDateTime start = (Timestamp.valueOf(rs.getString("Start")).toLocalDateTime().atZone(
                        ZoneId.of("UTC"))).withZoneSameInstant(ZoneId.systemDefault());
                ZonedDateTime end = (Timestamp.valueOf(rs.getString("End")).toLocalDateTime().atZone(
                        ZoneId.of("UTC"))).withZoneSameInstant(ZoneId.systemDefault());
                //creates a new class object and saves the information of the row values
                Appointment newAppointment = new Appointment(rs.getInt("Appointment_ID"),
                        rs.getString("Title"), rs.getString("Description"),
                        rs.getString("Location"), rs.getInt("Contact_ID"),
                        rs.getString("Type"),
                        start.withZoneSameInstant(ZoneId.systemDefault()),
                        end.withZoneSameInstant(ZoneId.systemDefault()),
                        rs.getInt("Customer_ID"), Integer.parseInt(rs.getString("User_ID")));

                //adds the new object to the list
                list.add(newAppointment);
            }
        }

        //returns the resulting list to the user
        return list;
    }

    /**
     * Retrieves the country string value based on a passed div id parameter
     * @param divID represents the division location that is within the target country
     * @return the string value of the country that has the division within it
     * @throws SQLException handles invalid div id parameter bounds
     */
    public String getCountryFromDiv(int divID) throws SQLException {
        //attempts to connect to the database, then retrieves the country id from the first_level_divisions table
        //as a sub-query to then look within the countries table to retrieve the string value
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM countries WHERE country_ID = " +
                     "(SELECT COUNTRY_ID FROM first_level_divisions WHERE Division_ID = " + divID + ");");
             ResultSet rs = ps.executeQuery()) {
            //loops through the resulting array
            while (rs.next()) {
                //the array should only have one matching value, returns the value
                return rs.getString("Country");
            }
        }
        //if no match is found, return a blank string
        return "";
    }

    /**
     * returns the division string value based on the parameter and primary key value division id in the
     * first_level_divisions table
     * @param divID representing the primary key within the database table
     * @return the string value of the matching division row
     * @throws SQLException handles invalid div id input
     */
    public String getDivisionFromDivID(int divID) throws SQLException {
        //attempts to connect to the database, then query for the row that has the matching division id value
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM first_level_divisions WHERE Division_ID = \"" + divID + "\";");
             ResultSet rs = ps.executeQuery()) {
            //loops through the results (should only be one
            while (rs.next()){
                //returns the value found
                return rs.getString("Division");
            }
        }
        //if there was no results found, return an empty string
        return "";
    }

    /**
     * returns the contacts from the table of the database and saves the values to the class object contact
     * @return the resulting list of contacts
     * @throws SQLException any invalid user or table values
     */
    public ObservableList<Contact> getContactArray() throws SQLException {
        //list to hold the resulting contacts list
        ObservableList<Contact> list = FXCollections.observableArrayList();

        //attempts to connect to the database and query for the contacts table
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM contacts");
             ResultSet rs = ps.executeQuery()) {
            //loops through the resulting set
            while(rs.next()){
                //saves the row values of the table to a new class object
                Contact newContact = new Contact(rs.getInt("Contact_ID"),
                        rs.getString("Contact_Name"), rs.getString("Email"));

                //adds the resulting object to the list
                list.add(newContact);
            }
        }

        //returns the resulting list
        return list;
    }

    /**
     * returns the users from the table of the database and saves the values to the class object user
     * @return the resulting list of users
     * @throws SQLException any invalid user or table values
     */
    public ObservableList<User> getUserArray() throws SQLException {
        //list to hold the resulting users list
        ObservableList<User> list = FXCollections.observableArrayList();

        //attempts to connect to the database and query for the users table
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
             ResultSet rs = ps.executeQuery()) {
            //loops through the resulting set
            while(rs.next()){
                //saves the row values of the table to a new class object
                User newUser = new User(Integer.parseInt(rs.getString("User_ID")), 
                        rs.getString("User_Name"));

                //adds the resulting object to the list
                list.add(newUser);
            }
        }

        //returns the resulting list
        return list;
    }
    
    /**
     * returns an array of all the contacts names
     * @return the string array of the different contacts
     * @throws SQLException handles invalid table values
     */
    public ObservableList<String> getContactNameArray() throws SQLException {
        //list to hold the resulting list of contacts
        ObservableList<Contact> contacts = getContactArray();

        //list of contacts to save the string values
        ObservableList<String> list = FXCollections.observableArrayList();

        //loops through the contacts list
        for (Contact contact : contacts) {
            //retrieves the name of each of the customers and adds to the list
            list.add(contact.getContactName());
        }

        //returns the resulting list
        return list;
    }

    /**
     * returns a string array of the different customer names
     * @return string array of customer names
     * @throws SQLException invalid table value protection
     */
    public ObservableList<String> getCustomerNameArray() throws SQLException {
        //list of customers
        ObservableList<Customer> customers = getCustomerArray();

        //holds the list of the customers' names
        ObservableList<String> customerNames = FXCollections.observableArrayList();

        //loops through all the different customers
        for (Customer customer : customers) {
            //retrieves the list of customers and retrieves their name to save to the list
            customerNames.add(customer.getCustomer_Name());
        }

        //returns the list of the customers names
        return customerNames;
    }

    /**
     * allows the user to add an appointment based on the passed in values
     * @param title the appointment title
     * @param description the appointment description
     * @param location the appointment location
     * @param contactID the appointment's contact id
     * @param type the appointments labeled type
     * @param startTime the appointments start time
     * @param endTime the appointments end time
     * @param customerId the appointments customer id
     * @param user_id the appointments creator, the correct user, represented by the user id
     * @return boolean statement to display results of the addition
     * @throws SQLException invalid user input protection
     */
    public boolean addAppointment(String title, String description, String location, int contactID, String type,
                                  ZonedDateTime startTime, ZonedDateTime endTime, int customerId, int user_id)
                                  throws SQLException {

        //attempts to connect to the database and call an insert into query with the passed parameter values assigned to
        //the different column values of the new row
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO appointments(Title," +
                     " Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) " +
                     "VALUES(\"" + title + "\", \"" + description + "\", \"" + location + "\", \"" + type + "\", \"" +
                     Timestamp.valueOf(startTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime()) + "\", \"" +
                     Timestamp.valueOf(endTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime()) + "\", \"" +
                     customerId + "\",\""+ user_id + "\",\"" + contactID + "\");" );
        ) {
            //returns the resulting boolean value of the success or failure of the insertion call
            return ps.execute();
        }
    }

    /**
     * attempts to modify an appointment based on the appointment id parameter, where any updated values are saved into
     * the database, over-writing any previously saved data
     * @param appointmentID hold the unchanged value of the appointment's id
     * @param title hold the new/unchanged value of the appointment's title
     * @param description hold the new/unchanged value of the appointment's description
     * @param location hold the new/unchanged value of the appointment's location
     * @param contactID hold the new/unchanged value of the appointment's contact id
     * @param type hold the new/unchanged value of the appointment's type
     * @param startTime hold the new/unchanged value of the appointment's start time
     * @param endTime hold the new/unchanged value of the appointment's end time
     * @param customerId hold the new/unchanged value of the appointment's customer id
     * @param userID
     * @return boolean value of success or failure of the update
     * @throws SQLException handles invalid user input values
     */
    public boolean modifyAppointment(int appointmentID, String title, String description, String location,
                                     int contactID, String type, ZonedDateTime startTime, ZonedDateTime endTime,
                                     int customerId, int userID) throws SQLException {
        //attempts to connect to the database and updates the column values of the row that matches the appointment id
        //value, based on the passed parameters
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("UPDATE appointments SET Title = \"" + title +
                     "\", Description = \"" + description + "\", Location = \"" +
                     location + "\", Contact_ID = \"" + contactID + "\", Type = \"" + type + "\", " + "Start =  \"" +
                     Timestamp.valueOf(startTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime()) +
                     "\", End = \"" +
                     Timestamp.valueOf(endTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime()) +
                     "\", Customer_ID = \"" + customerId + "\", User_ID = \"" + userID + "\" " +
                     "WHERE Appointment_ID = " + appointmentID + ";")
             //user is not edited as the original maker is the value to be saved
        ) {
            //returns the result of the update attempt
            return ps.execute();
        }
    }

    /**
     * gets the contact id based on the parameter of the same contact's name
     * @param contactName the string value of the full name of the contact
     * @return the integer value of the contact's id
     * @throws SQLException handles invalid contact name
     */
    public int getContactID(String contactName) throws SQLException {
        //attempts to connect to the database, and retrieves the customers that have a matching name value
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM contacts WHERE Contact_Name =" +
                     " \"" + contactName + "\";");
             ResultSet rs = ps.executeQuery()) {
            //loops through the resulting set
            while (rs.next()) {
                //assumes the correct contact is the first returned value
                return Integer.parseInt(rs.getString("Contact_ID"));
            }
        }

        //if no match is found returns a value of -1
        return -1;
    }

    /**
     * gets the customer id based on the parameter of the same customer's name
     * @param customerName the string value of the full name of the customer
     * @return the customer id value of the found customer
     * @throws SQLException handles invalid customerName value protection
     */
    public int getCustomerID(String customerName) throws SQLException {
        //attempts to connect to the database and retrieve the table of customers with a matching customer name
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers WHERE Customer_Name =" +
                     " \"" + customerName + "\";");
             ResultSet rs = ps.executeQuery()) {

            //loops through the resulting table
            while (rs.next()) {
                //returns the value of the customer id of the matching customer name
                return Integer.parseInt(rs.getString("Customer_ID"));
            }
        }

        //returns -1 if no customer name matched
        return -1;
    }

    /**
     * returns the contact's name based on the contact's id
     * @param contact_id the contact's id used to find the contact's name
     * @return the string value of the contact
     * @throws SQLException contact id invalid value exception
     */
    public String getContactName(int contact_id) throws SQLException {
        //attempts to connect to the database, and retrieves the contacts, whose id matches the table value
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM contacts WHERE Contact_ID =" +
                     " \"" + contact_id + "\";");
             ResultSet rs = ps.executeQuery()) {
            //loops through the resulting set
            while (rs.next()) {
                //returns the first found value of the contact's name
                return rs.getString("Contact_Name");
            }
        }
        //returns empty string if there was no match found
        return "";
    }

    /**
     * returns the customer's name based on the customer's id
     * @param customer_id to match the value of the table's primary key
     * @return string value of the customer's name based on the matching customer's id
     * @throws SQLException protects from invalid customer id values
     */
    public String getCustomerName(int customer_id) throws SQLException {
        //attempts to connect to the database and retrieve the table based on a matching customer id value
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers WHERE Customer_ID =" +
                     " \"" + customer_id + "\";");
             ResultSet rs = ps.executeQuery()) {
            //loops through the result and finds the matching customer name
            while (rs.next()) {
                //returns the first customer name value found
                return rs.getString("Customer_Name");
            }
        }
        //returns empty string if there was no match for the customer id
        return "";
    }

    /**
     * attempts to delete customer based on customer's id and restricted delete rules
     * @param customer_id the id of the customer who is attempting to be deleted
     * @return boolean value of the deletion status
     * @throws SQLException invalid customer id protection
     */
    public boolean deleteCustomer(int customer_id) throws SQLException {
        //checks to see if the customer has any appointments before trying to delete the customer
        if(!customerAppointmentCheck(customer_id)) {
            //attempts to call a delete from query with the assigned value to delete being the customer id
            try (Connection conn = DriverManager.getConnection(url, userName, password);
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM customers " +
                         "WHERE Customer_ID = " + customer_id + ";");) {
                //returns the status of the deletion
                return ps.execute();
            }
        }
        //if the customer has appointment return false to restrict delete
        else { return false; }
    }

    /**
     * checks to see if a customer with a matching customer id has any appointments in the database
     * @param customer_id the customer that is being checked for in the appointments
     * @return boolean value true if no appointments, false if there are appointments
     * @throws SQLException handles invalid customer_id value
     */
    private boolean customerAppointmentCheck(int customer_id) throws SQLException {
        //checks to see how many rows are returned from the search for the customer's id in the appointment table
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments " +
                     "WHERE Customer_ID = \"" + customer_id + "\";");
             ResultSet rs = ps.executeQuery()) {
            //if the resulting set is null, returns a false value
            if (!rs.next()) {return false;}
            }
        //if there was no matches to the appointment customer's return true
        return true;
    }

    /**
     * attempts to delete the appointment from the appointments database
     * @param appointment_id reference to the appointment to delete
     * @throws SQLException handles invalid appointment id value
     */
    public void deleteAppointment(int appointment_id) throws SQLException {
        //attempts to connect to the database and calls for a delete from query to delete the referenced appointment
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("DELETE FROM appointments " +
                     "WHERE Appointment_ID = " + appointment_id);) {
            //executes the query to delete the appointment
            ps.execute();
        }
    }

    /**
     * returns a list of the start and end times of the appointments that are associated with a customer
     * @param customer holds the customer's id value
     * @return a list of the start and end times of the appointments for each customer
     * @throws SQLException if invalid customer id value
     */
    public ObservableList<LocalDateTime[]> customerAssociatedAppointments(int customer) throws SQLException {
        //creates a list to hold the time values of the appointments
        ObservableList<LocalDateTime[]> list = FXCollections.observableArrayList();

        //attempts to connect to the database, and returns all associated appointments for the selected customer
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments " +
                     "WHERE Customer_ID = \"" + customer + "\";");
             ResultSet rs = ps.executeQuery()) {

            //loops through the results of the query
            while (rs.next()) {
                //format of the date and time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                //creates variable values from parsing the values found in the table
                LocalDateTime tempStart = LocalDateTime.parse(rs.getString("Start"), formatter);
                LocalDateTime tempEnd = LocalDateTime.parse(rs.getString("End"), formatter);

                //adds the found values to the list
                list.add(new LocalDateTime[]{tempStart, tempEnd});

            }
        }

        //returns the resulting list
        return list;
    }

    /**
     * returns a list of appointments for a selected contact based on the contact's id
     * @param contactId the contact's reference number
     * @return a list of appointments associated with the contact
     * @throws SQLException handles invalid user input or table values
     */
    public ObservableList<Appointment> getAppointmentArrayByContact(int contactId) throws SQLException {
        //list to hold the appointments found associated to the contact
        ObservableList<Appointment> list = FXCollections.observableArrayList();

        //attempts to connect to the database and retrieves the appointments that have a matching value in the
        //contact id field
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments WHERE Contact_ID = "
                     + contactId + ";");
             ResultSet rs = ps.executeQuery()) {

            //loops through the results associated with the contact
            while(rs.next()){
                //creates a format for the date values to be saved
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                LocalDateTime startTime = LocalDateTime.parse(rs.getString("Start"), formatter);
                LocalDateTime endTime = LocalDateTime.parse(rs.getString("End"), formatter);

                //creates temp appointment object to be saved into the list
                Appointment newAppointment = new Appointment(rs.getInt("Appointment_ID"),
                        rs.getString("Title"), rs.getString("Description"),
                        rs.getString("Location"), rs.getInt("Contact_ID"),
                        rs.getString("Type"),
                        startTime.atZone(ZoneId.systemDefault()),
                        endTime.atZone(ZoneId.systemDefault()),
                        Integer.parseInt(rs.getString("Customer_ID")),
                        Integer.parseInt(rs.getString("User_ID")));

                //adds the new temp appointment to the list
                list.add(newAppointment);
            }
        }

        //returns the resulting list of appointments associated with the contact
        return list;
    }

    /**
     * returns an array of all the users names
     * @return the string array of the different users
     * @throws SQLException handles invalid table values
     */
    public ObservableList<String> getUserNameArray() throws SQLException {
        //list to hold the resulting list of contacts
        ObservableList<User> users = getUserArray();

        //list of contacts to save the string values
        ObservableList<String> list = FXCollections.observableArrayList();

        //loops through the contacts list
        for (User user : users) {
            //retrieves the name of each of the customers and adds to the list
            list.add(user.getUserName());
        }

        //returns the resulting list
        return list;
    }

    /**
     * gets the user id based on the parameter of the same user's name
     * @param usersUserName the string value of the full name of the user
     * @return the integer value of the user's id
     * @throws SQLException handles invalid userName
     */
    public int getUserID(String usersUserName) throws SQLException {
        //attempts to connect to the database, and retrieves the users that have a matching name value
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE User_Name =" +
                     " \"" + usersUserName + "\";");
             ResultSet rs = ps.executeQuery()) {
            //loops through the resulting set
            while (rs.next()) {
                //assumes the correct user is the first returned value
                return Integer.parseInt(rs.getString("User_ID"));
            }
        }

        //if no match is found returns a value of -1
        return -1;
    }

    /**
     * returns the user's name based on the user's id
     * @param user_id to match the value of the table's primary key
     * @return string value of the user's name based on the matching user's id
     * @throws SQLException protects from invalid user id values
     */
    public String getUserName(int user_id) throws SQLException {
        //attempts to connect to the database and retrieve the table based on a matching user id value
        try (Connection conn = DriverManager.getConnection(url,userName,password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE User_ID =" +
                     " \"" + user_id + "\";");
             ResultSet rs = ps.executeQuery()) {
            //loops through the result and finds the matching user name
            while (rs.next()) {
                //returns the first user name value found
                return rs.getString("User_Name");
            }
        }
        //returns empty string if there was no match for the user id
        return "";
    }
}
