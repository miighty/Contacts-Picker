package es.alfongj.contactswrv.presenter;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import es.alfongj.contactswrv.R;
import es.alfongj.contactswrv.model.Contact;
import es.alfongj.contactswrv.ui.AddContact;
import es.alfongj.contactswrv.ui.ContactsAdapter;
/**
 * Fragment that holds the RecyclerViews
 */

public class ContactsFragment extends Fragment implements SearchView.OnQueryTextListener {
        
    public  List<Contact> contactsList ;
    public ContactsAdapter mAdapter;
    private Cursor mCursor;
    private int  mIdColIdx, lookupIdx, emailIdx, contactIDIdx;
    private RecyclerView mContactListView;
    private Button mAddButton;
    
    // Empty public constructor, required by the system
    public ContactsFragment() {}
    
    private String[] PROJECTION = new String[] {
    ContactsContract.Data._ID,
    ContactsContract.Data.LOOKUP_KEY,
    ContactsContract.CommonDataKinds.Email.DATA,
    ContactsContract.Contacts.DISPLAY_NAME,
    ContactsContract.CommonDataKinds.Photo.CONTACT_ID
    };
    
    String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
    
    // Defines a variable for the search string
    private String order = "CASE WHEN "
    + ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME
    + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
    + ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME
    + ", "
    + ContactsContract.CommonDataKinds.Email.DATA
    + " COLLATE NOCASE";
    
    // A UI Fragment must inflate its View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        
        // Inflate the fragment layout
        View root = inflater.inflate(R.layout.contacts_list, container, false);
        mContactListView = (RecyclerView) root.findViewById(R.id.rv_contact_list);
        
        mAddButton = (Button) root.findViewById(R.id.button2);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_host,
                         new AddContact()).addToBackStack("Initial").commit();
            }
        });

        mContactListView.setItemAnimator(new DefaultItemAnimator());
        
        contactsList = new ArrayList<Contact>();
        
        mAdapter = new ContactsAdapter(getActivity(), contactsList);
        
        mContactListView.setAdapter(mAdapter);
        
        final RecyclerViewFastScroller fastScroller = (RecyclerViewFastScroller) root.findViewById(R.id.fastscroller);
        mContactListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                //TODO if the items are filtered, considered hiding the fast scroller here
                final int firstVisibleItemPosition = findFirstVisibleItemPosition();
                if (firstVisibleItemPosition != 0) {
                    // this avoids trying to handle un-needed calls
                    if (firstVisibleItemPosition == -1)
                        //not initialized, or no items shown, so hide fast-scroller
                        fastScroller.setVisibility(View.GONE);
                    return;
                }
                final int lastVisibleItemPosition = findLastVisibleItemPosition();
                int itemsShown = lastVisibleItemPosition - firstVisibleItemPosition + 1;
                //if all items are shown, hide the fast-scroller
                fastScroller.setVisibility(mAdapter.getItemCount() > itemsShown ? View.VISIBLE : View.GONE);
            }
        });
        fastScroller.setRecyclerView(mContactListView);
        fastScroller.setViewsToUse(R.layout.recycler_view_fast_scroller__fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((MainActivity.addedContacts.size() >0) && (contactsList.size() != MainActivity.totalContacts.size())){
            Contact contact = MainActivity.addedContacts.get(MainActivity.addedContacts.size() - 1);
            if ((contact != null) && !(contact.emails.get(0) == null)) {
                //generate a 4 digit integer 1000 <10000
                int randomPIN = (int) (Math.random() * 9000) + 1000;
                
                contact.contactID = String.valueOf(randomPIN);
                
                mAdapter.selected.add(contact.contactID);
                MainActivity.totalContacts.add(contact);
                contactsList.add(contact);
            }
            
        }
        sortContacts();
        
        mAdapter = new ContactsAdapter(getActivity(), contactsList);
        
        mContactListView.setAdapter(mAdapter);
    }
    
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        
        mAdapter = new ContactsAdapter(getActivity(), contactsList);
        
        mContactListView.setAdapter(mAdapter);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        // Initializes a loader for loading the contacts
        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                
                Uri contentUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
                
                return new CursorLoader(
                                        getActivity(),
                                        contentUri,
                                        PROJECTION,
                                        filter,
                                        null,
                                        null
                                        );
                
            }
            
            @Override
            public void onLoadFinished(Loader<Cursor> objectLoader, Cursor cursor) {
                long startTime = System.currentTimeMillis();
                long endTime;
                
                contactsList = new ArrayList<Contact>();
                mIdColIdx = cursor.getColumnIndex(ContactsContract.Data._ID);
                lookupIdx = cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY);
                emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                contactIDIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.CONTACT_ID);
                mCursor = cursor;
                
                ContentResolver cr = getActivity().getContentResolver();
                
                HashMap<String, Contact> shortContacts = new HashMap<String, Contact>();
                
                StringBuilder contactIDs = new StringBuilder();
                
                for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                    
                    String firstName = null;
                    String lastName = null;
                    List<String> emailAddresses = new ArrayList<String>() ;
                    String email = mCursor.getString(emailIdx);
                    String contactIndex = mCursor.getString(contactIDIdx);
                    long contactLookUp = mCursor.getLong(lookupIdx);
                    emailAddresses.add(email);
                    Uri contactUri =  ContactsContract.Contacts.getLookupUri(mCursor.getLong(mIdColIdx), mCursor.getString(lookupIdx));
                    
                    Contact c = new Contact(firstName, lastName, contactIndex, contactLookUp, emailAddresses, contactUri);
                    shortContacts.put(contactIndex, c);
                    contactsList.add(c);
                    MainActivity.totalContacts.add(c);
                    
                    if (contactIDs.length() != 0) {
                        contactIDs.append(",");
                    }
                    contactIDs.append(contactIndex);
                    
                }
                
                String[] projectionNames = {
                    ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID,
                    ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                    ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                    ContactsContract.Data.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME
                };
                
                
                Cursor namesCursor = cr.query(ContactsContract.Data.CONTENT_URI, projectionNames,
                                              ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " IN (" + contactIDs.toString() + ") AND " +
                                              ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'",
                                              null, null);
                while (namesCursor.moveToNext()) {
                    String id = namesCursor.getString(namesCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID));
                    
                    Contact c = shortContacts.get(id);
                    
                    c.firstName = namesCursor.getString(namesCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                    c.lastName = namesCursor.getString(namesCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                    c.displayName = c.firstName +" "+c.lastName;
                    
                }
                
                namesCursor.close();
                sortContacts();
                
                mAdapter = new ContactsAdapter(getActivity(), contactsList);
                mContactListView.setAdapter(mAdapter);
                endTime = System.currentTimeMillis();
                
                long timeneeded =  ((startTime - endTime));
                Log.wtf("Time taken for loop", String.valueOf(timeneeded));
            }
            
            @Override
            public void onLoaderReset(Loader<Cursor> cursorLoader) {
                // Set the adapter again but nullify the first one
                mContactListView.setAdapter(mAdapter);
                
            }
        });
    }
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contact_list_menu, menu);
        
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(true);
        
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search by First or Last Name");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(true);
        
    }
    
    @Override
    public boolean onQueryTextChange(String query) {
        
        if (query.length()  == 0) {
            final List<Contact> filteredModelList = filter(contactsList, "");
            mAdapter.animateTo(filteredModelList);
            mContactListView.scrollToPosition(0);
            return true;
        }
        
        if (query.length() >0) {
            final List<Contact> filteredModelList = filter(contactsList, query);
            mAdapter.animateTo(filteredModelList);
            mContactListView.scrollToPosition(0);
            return true;
        }
        
        return false;
    }
    
    
    private List<Contact> filter(List<Contact> models, String query) {
        
        query = query.toLowerCase();
        
        final List<Contact> filteredModelList = new ArrayList<>();
        for (Contact model : models) {
            if (model.displayName!= null ) {
                final String text = model.displayName.toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
    }
    
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    
    public void sortContacts() {
        
        Collections.sort(contactsList, new Comparator<Contact>() {
            public int compare(Contact cont1, Contact cont2) {
                return cont1.getLastName().compareToIgnoreCase(cont2.getLastName());
                
            }
        });
    }
}
