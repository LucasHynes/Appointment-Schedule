import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Sets the class to hold the information of the appointments within the database to be able to access through out the
 * program
 * @author Lucas Hynes
 * @version 1.0
 * @since 11/20/2020
 */
public class Appointment {
    private int Appointment_ID; //holds the id of the appointment
    private String Title; //holds the title of the appointment
    private String Description; //holds the description of the appointment
    private String Location; //holds the location of the appointment
    private int Contact_ID; //holds the id of the contact assigned to the appointment
    private String Type; //holds the type of appointment
    private ZonedDateTime Start; //holds the start time of the appointment
    private ZonedDateTime End; //holds the end time of the appointment
    private int Customer_ID; //holds the customer's id that is assigned to the appointment
    private int User_ID;

    /**
     * the constructor class for the appointment
     * @param appointment_id holds the appointment id
     * @param title holds appointment title
     * @param description holds appointment description
     * @param location holds appointment location
     * @param contact_id holds appointment contact
     * @param type holds appointment type
     * @param start holds appointment start time
     * @param end holds appointment end time
     * @param customer_id holds appointment customer
     * @param user_id
     */
    public Appointment(int appointment_id, String title, String description, String location, int contact_id,
                       String type, ZonedDateTime start, ZonedDateTime end, int customer_id, int user_id) {
        //assign parameter values to the variable instances of the class
        this.Appointment_ID = appointment_id;
        this.Title = title;
        this.Description = description;
        this.Location = location;
        this.Contact_ID = contact_id;
        this.Type = type;
        this.Start = start;
        this.End = end;
        this.Customer_ID = customer_id;
        User_ID = user_id;
    }

    /**
     * @return the appointment id of the appointment
     */
    public int getAppointment_ID() {
        return Appointment_ID;
    }

    /**
     * @param appointment_ID returns appointment id
     */
    public void setAppointment_ID(int appointment_ID) {
        Appointment_ID = appointment_ID;
    }

    /**
     * @return the title of the appointment
     */
    public String getTitle() {
        return Title;
    }

    /**
     * @param title set to class
     */
    public void setTitle(String title) {
        Title = title;
    }

    /**
     * @return the description of the appointment
     */
    public String getDescription() {
        return Description;
    }

    /**
     * @param description sets to class
     */
    public void setDescription(String description) {
        Description = description;
    }

    /**
     * @return the location of the appointment
     */
    public String getLocation() {
        return Location;
    }

    /**
     * @param location sets to class
     */
    public void setLocation(String location) {
        Location = location;
    }

    /**
     * @return the contact id of the appointment
     */
    public int getContact_ID() {
        return Contact_ID;
    }

    /**
     * @param contact_ID sets to class
     */
    public void setContact_ID(int contact_ID) {
        Contact_ID = contact_ID;
    }

    /**
     * @return the type of appointment
     */
    public String getType() {
        return Type;
    }

    /**
     * @param type sets to class
     */
    public void setType(String type) {
        Type = type;
    }

    /**
     * @return the start time of the appointment in the zone date time format
     */
    public ZonedDateTime getStart() { return Start.withZoneSameInstant(ZoneId.systemDefault()); }

    /**
     * @param start sets to class
     */
    public void setStart(ZonedDateTime start) {
        Start = start.withZoneSameInstant(ZoneId.systemDefault());
    }

    /**
     * @return the end time of the appointment in the zone date time format
     */
    public ZonedDateTime getEnd() { return End.withZoneSameInstant(ZoneId.systemDefault()); }

    /**
     * @param end sets to class
     */
    public void setEnd(ZonedDateTime end) {
        End = end.withZoneSameInstant(ZoneId.systemDefault());
    }

    /**
     * @return the customer id of the appointment
     */
    public int getCustomer_ID() {
        return Customer_ID;
    }

    /**
     * @param customer_ID sets to class
     */
    public void setCustomer_ID(int customer_ID) {
        Customer_ID = customer_ID;
    }

    /**
     * @param user_ID sets to class
     */
    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    /**
     * @return the user id of the appointment
     */
    public int getUser_ID() {return User_ID;}
}
