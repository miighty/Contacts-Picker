package es.alfongj.contactswrv.ui;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.alfongj.contactswrv.R;
import es.alfongj.contactswrv.model.Contact;
import es.alfongj.contactswrv.presenter.MainActivity;
import es.alfongj.contactswrv.presenter.RecyclerViewFastScroller;

public class ContactsAdapter extends RecyclerView.Adapter<ContactViewHolder>  implements RecyclerViewFastScroller.BubbleTextGetter {
    
    public List<Contact> theContacts;
    private final LayoutInflater mInflater;
    public static List<String> selected = new ArrayList<String>();
    
    public ContactsAdapter(Context context, List<Contact> contactElements) {
        mInflater = LayoutInflater.from(context);
        theContacts =  new ArrayList<>(contactElements);
    }
    
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        View listItemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.contacts_list_item, parent, false);
        
        listItemView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                notifyDataSetChanged();
                
            }
        });
        
        return new ContactViewHolder(listItemView);
    }
    
    
    public void setModels(List<Contact> models) {
        theContacts = new ArrayList<>(models);
    }
    
    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int pos) {
        final Contact model = theContacts.get(pos);
        contactViewHolder.bind(model);
    }
    
    @Override
    public int getItemCount() {
        return theContacts.size();
    }
    
    public Contact removeItem(int position) {
        final Contact model = theContacts.remove(position);
        notifyItemRemoved(position);
        return model;
    }
    
    public void addItem(int position,Contact model) {
        theContacts.add(position, model);
        notifyItemInserted(position);
    }
    
    public void moveItem(int fromPosition, int toPosition) {
        final Contact model = theContacts.remove(fromPosition);
        
        theContacts.add(toPosition, model);
        
        notifyItemMoved(fromPosition, toPosition);
    }
    
    public void animateTo(List<Contact> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }
    
    private void applyAndAnimateRemovals(List<Contact> newModels) {
        for (int i = theContacts.size() - 1; i >= 0; i--) {
            final Contact model = theContacts.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }
    
    private void applyAndAnimateAdditions(List<Contact> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Contact model = newModels.get(i);
            if (!theContacts.contains(model)) {
                addItem(i, model);
            }
        }
    }
    
    private void applyAndAnimateMovedItems(List<Contact> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Contact model = newModels.get(toPosition);
            final int fromPosition = theContacts.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
    
    @Override
    public String getTextToShowInBubble(int pos) {
        return Character.toString(theContacts.get(pos).lastName.charAt(0));
    }
}
