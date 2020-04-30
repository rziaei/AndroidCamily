package edu.murraystate.androidcamilydashboard.ui.login;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import edu.murraystate.androidcamilydashboard.MainActivity;
import edu.murraystate.androidcamilydashboard.R;

public class activity_login extends AppCompatActivity {
    Button loginButton;
    EditText nameEdit, userEdit;


    private static final String TAG = activity_login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameEdit = (EditText) findViewById(R.id.name);
        userEdit = (EditText) findViewById(R.id.username);
        loginButton = (Button) findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameEdit.getText().toString().equals("") || userEdit.getText().toString().equals(""))
                {
                    Toast.makeText(activity_login.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(activity_login.this, "Welcome, " + nameEdit.getText().toString() + ".", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("nickname", nameEdit.getText().toString());
                    intent.putExtra("username", userEdit.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
}

