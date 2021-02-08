/**
 * This is the class used to represent the contacts that are used within the appointment field
 * @author Lucas Hynes
 * @version 1.0
 * @since 11/20/2020
 */
public class Contact {
    private int contactID; //holds the id of the contact
    private String contactName; //holds the name of the contact
    private String email; //holds the email of the contact

    /**
     * constructor class to initialize values
     * @param contactID represents the contact id
     * @param contactName represents the contact name
     * @param email represents the contact's email
     */
    public Contact(int contactID, String contactName, String email) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.email = email;
    }

    /**
     * @return the contact id
     */
    public int getContactID() {
        return contactID;
    }

    /**
     * @param contactID stores value to class
     */
    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    /**
     * @return the contact name
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @param contactName stores value to class
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * @return the contact email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email stores value to class
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
