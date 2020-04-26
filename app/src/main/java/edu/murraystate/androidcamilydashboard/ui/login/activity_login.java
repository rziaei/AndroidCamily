package edu.murraystate.androidcamilydashboard.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import edu.murraystate.androidcamilydashboard.MainActivity;
import edu.murraystate.androidcamilydashboard.R;

public class activity_login extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    public String nickname;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText nicknameEditText = findViewById(R.id.nickname);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

                loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
                    @Override
                    public void onChanged(@Nullable LoginFormState loginFormState) {
                        if (loginFormState == null) {
                            return;
                        }
                        loginButton.setEnabled(loginFormState.isDataValid());
                        if (loginFormState.getUsernameError() != null) {
                            usernameEditText.setError(getString(loginFormState.getUsernameError()));
                        }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                System.out.println("Button Clicked");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("username", usernameEditText.getText().toString());
                //intent.putExtra("password", passwordEditText.getText());
                intent.putExtra("nickname", nicknameEditText.getText().toString());
                nickname = nicknameEditText.getText().toString();
                startActivity(intent);
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = "Logged in with name: " + nickname;
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

   //public void sendLogin(String name, String pass, String nick) {
   //    FirebaseFirestore db = FirebaseFirestore.getInstance();
   //    Map<String, Object> user = new HashMap<>();
   //    user.put("Name", name);
   //    user.put("Password", pass);
   //    user.put("Nickname", nick);

   //    db.collection("login").document(name)
   //            .set(user)
   //            .addOnSuccessListener(new OnSuccessListener<Void>() {
   //                @Override
   //                public void onSuccess(Void aVoid) {
   //                    Log.d("DB_RESULT", "User successfully updated");
   //                }
   //            })
   //            .addOnFailureListener(new OnFailureListener() {
   //                @Override
   //                public void onFailure(@NonNull Exception e) {
   //                    Log.w("DB_RESULT", "Update unsuccessful", e);
   //                }
   //            });
    //}
}
