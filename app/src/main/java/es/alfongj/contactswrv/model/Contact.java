package es.alfongj.contactswrv.model;

import android.net.Uri;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity that represents a contact
 */
public class Contact  {

    public String contactID;
    public long contactLookUp;
    public String displayName;
    public String firstName;
    public String lastName;
    public List emails;
    public Uri contactUri;

    public String getLastName() {
        if (this.lastName == null ) {
            this.lastName = " ";
        }
        return this.lastName;
    }

    public List<String> getEmails() {
        return this.emails;
    }

    public Contact(String firstName, String lastName,String contactID, long contactLookUp,
                   List emailAddresses, Uri contactUri) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emails = emailAddresses;
        this.contactID = contactID;
        this.contactLookUp = contactLookUp;
        this.contactUri  = contactUri;
    }
}
