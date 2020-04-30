package edu.murraystate.androidcamilydashboard.Useless.ToDo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import edu.murraystate.androidcamilydashboard.R;

public class Activity_ToDoList extends AppCompatActivity {
    ListView listView;
    ArrayList<String> arrayListToDo;
    ArrayAdapter<String> arrayAdapterToDo;
    String massageText;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);
        listView = (ListView) findViewById(R.id.Listview);
        arrayListToDo = new ArrayList<>();
        arrayAdapterToDo = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListToDo);
        listView.setAdapter(arrayAdapterToDo);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(Activity_ToDoList.this, EditMessageClass.class);
                intent.putExtra(Intent_Constants.INTENT_MESSAGE_DATA, arrayListToDo.get(position).toString());
                intent.putExtra(Intent_Constants.INTENT_ITEM_POSITION, position);
                startActivityForResult(intent, Intent_Constants.INTENT_REQUEST_CODE_TWO);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                arrayListToDo.remove(position);
                arrayAdapterToDo.notifyDataSetChanged();
                return true;
            }
        });
        try {

            Scanner sc = new Scanner(openFileInput("Todo.txt"));
            while (sc.hasNextLine()) {
                String data = sc.nextLine();
                arrayAdapterToDo.add(data);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("what would you like to do");
        String options[] = {"Delete", "Cancel"};
        for (String option : options) {
            menu.add(option);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            PrintWriter pw = new PrintWriter(openFileOutput("Todo.txt", Context.MODE_PRIVATE));
            for (String data : arrayListToDo) {
                pw.println(data);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finish();
    }

    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(Activity_ToDoList.this, EditFieldClass.class);
        startActivityForResult(intent, Intent_Constants.INTENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Intent_Constants.INTENT_REQUEST_CODE) {
            massageText = data.getStringExtra(Intent_Constants.INTENT_MASSAGE_FIELD);
            arrayListToDo.add(massageText);
            arrayAdapterToDo.notifyDataSetChanged();
        } else if (resultCode == Intent_Constants.INTENT_REQUEST_CODE_TWO) {
            massageText = data.getStringExtra(Intent_Constants.INTENT_CHANGED_MESSAGE);
            position = data.getIntExtra(Intent_Constants.INTENT_ITEM_POSITION, -1);
            arrayListToDo.remove(position);
            arrayListToDo.add(position, massageText);
            arrayAdapterToDo.notifyDataSetChanged();
        }
    }
}