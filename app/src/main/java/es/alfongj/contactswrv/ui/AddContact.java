package es.alfongj.contactswrv.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import es.alfongj.contactswrv.R;
import es.alfongj.contactswrv.model.Contact;
import es.alfongj.contactswrv.presenter.MainActivity;

/**
 * Created by RicardoMighty on 3/31/16.
 */

public class AddContact extends Fragment {
    
    EditText firstnameOfContact;
    EditText emailOfContact;
    EditText lastnameOfContact;
    
    public AddContact() {}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View root = inflater.inflate(R.layout.add_contact_fragment, container, false);
        setHasOptionsMenu(true);
        
        firstnameOfContact = (EditText) root.findViewById(R.id.firstname_label);
        lastnameOfContact = (EditText) root.findViewById(R.id.lastname_label);
        emailOfContact = (EditText) root.findViewById(R.id.email_label);
        return root;
    }
    
    @Override
    public void onCreateOptionsMenu(
                                    Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_menu_item, menu);
        MenuItem item = menu.findItem(R.id.edit_item);
        item.setVisible(true);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.edit_item:
                
                getFragmentManager().popBackStack("Initial",
                                                  FragmentManager.POP_BACK_STACK_INCLUSIVE);
                
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        String firstName = firstnameOfContact.getText().toString();
        String lastName = lastnameOfContact.getText().toString();
        String email = emailOfContact.getText().toString();
        
        if ((firstName != null) && (lastName != null) && (email.length() > 0)) {
            MainActivity.addContact(firstName,lastName,email);
        }
    }
    
}
