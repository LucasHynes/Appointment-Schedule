import java.sql.SQLException;

/**
 * Class to represent the customers assigned to the appointments and information stored
 * @author Lucas Hynes
 * @version 1.0
 * @since 11/20/2020
 */
public class Customer {
    private int Customer_ID; //holds the customers id
    private String Customer_Name; //holds the customers name
    private String Address; //holds the customers address
    private String Postal_Code; //holds the customers postal code
    private String Phone; //holds the customers phone number
    private int Division_ID; //hold the customers division id

    /**
     * Constructor to initialize values stored in the class
     * @param customer_id represents the customers id
     * @param customer_name represents the customers name
     * @param address represents the customers address
     * @param postal_code represents the customers postal code
     * @param phone represents the customers phone number
     * @param division_ID represents the customers division id
     */
    public Customer(int customer_id, String customer_name, String address, String postal_code, String phone, int division_ID) {
        this.Customer_ID = customer_id;
        this.Customer_Name = customer_name;
        this.Address = address;
        this.Postal_Code = postal_code;
        this.Phone = phone;
        this.Division_ID = division_ID;
    }

    /**
     * @return the customers id
     */
    public int getCustomer_ID() {
        return Customer_ID;
    }

    /**
     * @param customer_ID stored value to class
     */
    public void setCustomer_ID(int customer_ID) {
        Customer_ID = customer_ID;
    }

    /**
     * @return the customers name
     */
    public String getCustomer_Name() {
        return Customer_Name;
    }

    /**
     * @param customer_Name stored value to class
     */
    public void setCustomer_Name(String customer_Name) {
        Customer_Name = customer_Name;
    }

    /**
     * @return the customers address
     */
    public String getAddress() {
        return Address;
    }

    /**
     * @param address stored value to class
     */
    public void setAddress(String address) {
        Address = address;
    }

    /**
     * @return the customers zip code
     */
    public String getPostal_Code() {
        return Postal_Code;
    }

    /**
     * @param postal_Code stored value to class
     */
    public void setPostal_Code(String postal_Code) {
        Postal_Code = postal_Code;
    }

    /**
     * @return the customers phone number stored as a string value
     */
    public String getPhone() {
        return Phone;
    }

    /**
     * @param phone stored value to class
     */
    public void setPhone(String phone) {
        Phone = phone;
    }

    /**
     * returns the country the user is from
     * @return the string value of the country
     * @throws SQLException handles invalid table values
     */
    public String getCountry() throws SQLException {
        //initializes method to access database methods
        DBAccess data = new DBAccess();
        //returns the result of the method call
        return data.getCountryFromDiv(getDivision_ID());
    }

    /**
     * @return the customers division id (state/territory/province/etc.)
     */
    public int getDivision_ID() {
        return Division_ID;
    }

    /**
     * @param division_ID stored value to class
     */
    public void setDivision_ID(int division_ID) {
        Division_ID = division_ID;
    }

    /**
     * gets the string of the division value
     * @return the string division name
     * @throws SQLException handles invalid table values
     */
    public String getDivision() throws SQLException {
        //initializes method to access database methods
        DBAccess data = new DBAccess();
        //returns the result of the method call
        return data.getDivisionFromDivID(getDivision_ID());
    }
}
