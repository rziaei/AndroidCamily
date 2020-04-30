package edu.murraystate.androidcamilydashboard.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.murraystate.androidcamilydashboard.R;

public class activity_locator_contact extends AppCompatActivity {

    ListView listView;
    ArrayList<Contact> arrayListContact;
    ArrayAdapter<Contact> arrayAdapterContact;

    private HashMap<String, Contact> contact = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator_contact);
        listView = (ListView) findViewById (R.id.ListView);

        arrayListContact = new ArrayList <> ();
        arrayAdapterContact = new ArrayAdapter <> (this, android.R.layout.simple_list_item_1, arrayListContact);
        listView.setAdapter (arrayAdapterContact);

        Intent intent = getIntent();
        contact = (HashMap<String, Contact>) intent.getSerializableExtra("contact");

        Iterator contactIterator = contact.entrySet().iterator();

        while (contactIterator.hasNext()) {
            HashMap.Entry element = (HashMap.Entry)contactIterator.next();
            Contact current = (Contact) element.getValue();
            arrayListContact.add(current);
            arrayAdapterContact.notifyDataSetChanged();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent i = new Intent();

                i.putExtra("Selected", (Parcelable) listView.getItemAtPosition(position));
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }
}
