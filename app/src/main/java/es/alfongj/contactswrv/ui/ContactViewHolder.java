package es.alfongj.contactswrv.ui;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import es.alfongj.contactswrv.R;
import es.alfongj.contactswrv.framework.widgets.RoundedImageView;
import es.alfongj.contactswrv.model.Contact;

/**
 * Contains a Contact List Item
 */

public class ContactViewHolder extends RecyclerView.ViewHolder {
    
    private RoundedImageView mImage;
    private TextView firstnameLabel;
    private TextView lastnameLabel;
    private TextView emailLabel;
    private Contact mBoundContact; // Can be null
    private CheckBox mRadio;
    
    public ContactViewHolder(final View itemView) {
        super(itemView);
        mImage = (RoundedImageView) itemView.findViewById(R.id.rounded_iv_profile);
        firstnameLabel = (TextView) itemView.findViewById(R.id.firstname_label);
        lastnameLabel = (TextView) itemView.findViewById(R.id.surname_label);
        emailLabel = (TextView) itemView.findViewById(R.id.email_label);
        mRadio = (CheckBox) itemView.findViewById(R.id.checkbox);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBoundContact != null) {
                    
                }
            }
        });
    }
    
    public void bind(final Contact contact) {
        mBoundContact = contact;
        firstnameLabel.setText(contact.firstName);
        lastnameLabel.setText(contact.lastName);
        List<String> emailAddresses = contact.getEmails();
        if ( emailAddresses.size() > 1) {
            int amountOfEmails = emailAddresses.size();
            emailLabel.setText(contact.getEmails().get(0) + " + "+ amountOfEmails +"more" );
        }
        emailLabel.setText(contact.getEmails().get(0));
        
        if (ContactsAdapter.selected.contains(contact.contactID)) {
            mRadio.setChecked(true);
        } else {
            mRadio.setChecked(false);
        }
        mRadio.setTag(contact.contactID);
        
        mRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (!(mRadio.isChecked())) {
                    ContactsAdapter.selected.remove(contact.contactID);
                    mRadio.setChecked(false);
                } else {
                    ContactsAdapter.selected.add(contact.contactID);
                    mRadio.setChecked(true);
                }
                
                //mRadio.setChecked(selected.contains(contact.contactUri));
                
                Log.wtf("This is selected: ", ContactsAdapter.selected.toString());
                
            }
            
        });
        
        Picasso.with(itemView.getContext())
        .load(contact.contactUri)
        .placeholder(R.drawable.ic_launcher)
        .error(R.drawable.ic_launcher)
        .into(mImage);
    }
    
}
