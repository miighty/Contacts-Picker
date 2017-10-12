package es.alfongj.contactswrv.presenter;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import es.alfongj.contactswrv.R;
import es.alfongj.contactswrv.model.Contact;

public class MainActivity extends ActionBarActivity {
    public static List<Contact> addedContacts = new ArrayList<Contact>();
    public static List<Contact> totalContacts = new ArrayList<Contact>();
    public static Contact newestContact = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        getSupportFragmentManager().beginTransaction()
        .add(R.id.fragment_host, new ContactsFragment())
        .commit();
    }
    
    public static View.OnClickListener click() {
        return null;
    }
    
    public static Contact addContact(String fName, String lName, String email ) {
        List<String> emailAddresses = new ArrayList<String>();
        emailAddresses.add(email);
        Contact newContact =  new Contact(fName,lName,null,0,emailAddresses,null);
        newContact.displayName =  newContact.firstName +" "+newContact.lastName;
        addedContacts.add(newContact);
        newestContact = newContact;
        
        return newContact;
    }
    
}
