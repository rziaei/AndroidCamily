package edu.murraystate.androidcamilydashboard;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;

import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

import edu.murraystate.androidcamilydashboard.Useless.ToDo.Activity_ToDoList;

public class MainActivity extends AppCompatActivity implements
        LocationListener {
    LinearLayout rellay_todolist;
    LinearLayout rellay_personal;
    LinearLayout rellay_kitchen;
    LinearLayout rellay_familylocator;

    protected FusedLocationProviderClient mFused;
    protected Location current;
    protected LocationRequest locRequest;
    protected LocationCallback locCallback;
    protected LocationSettingsRequest locSettingsRequest;
    protected SettingsClient settingsClient;
    protected boolean requestingLocationUpdates;

    private FirebaseFunctions mFunctions;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = activity_familylocator.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private static final String NAME_KEY = "Name";
    private static final String LAT_KEY = "Latitude";
    private static final String LONG_KEY = "Longitude";
    private static final String PHONE_KEY = "Phone Number";

    protected static String name;
    protected static String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestingLocationUpdates = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        Intent intent = getIntent();
        if (extras!=null) {
            name = extras.getString("username");
            nickname = extras.getString("nickname");
        }

        Log.i("test", name.toString());

        rellay_todolist = findViewById(R.id.rellay_todolist);
        rellay_personal = findViewById(R.id.rellay_personal);
        rellay_kitchen = findViewById(R.id.rellay_kitchen);
        rellay_familylocator = findViewById(R.id.rellay_familylocator);

        rellay_todolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Activity_ToDoList.class);
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        rellay_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, activity_personal.class);
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        rellay_kitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, activity_kitchen.class);
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        rellay_familylocator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, activity_familylocator.class);
                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        // Location services
        mFused = LocationServices.getFusedLocationProviderClient(this);
        mFused.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                current = location;
            }
        });
        settingsClient = LocationServices.getSettingsClient(this);
        createLocationRequest();
        createLocationCallback();
        buildLocationSettingsRequest();
        mFunctions = FirebaseFunctions.getInstance();
    }

    protected void createLocationRequest() {
        locRequest = LocationRequest.create();
        locRequest.setInterval(5000);
        locRequest.setFastestInterval(3000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locRequest);
        locSettingsRequest = builder.build();
    }

    private void createLocationCallback() {
        locCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locResult) {
                Log.i("Update", "Updating Location");
                super.onLocationResult(locResult);
                current = locResult.getLastLocation();
                sendLocation();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        settingsClient.checkLocationSettings(locSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locSettingsResponse) {
                        mFused.requestLocationUpdates(locRequest,
                                locCallback,
                                Looper.getMainLooper());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                        }
                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {
        current = location;
    }

    public void sendLocation() {
        Log.i(TAG, "Location sent");
        Map<String, Object> user = new HashMap<>();
        user.put("Name", nickname);
        user.put("Phone:", "+12345678");
        user.put("Latitude", current.getLatitude());
        user.put("Longitude", current.getLongitude());

        db.collection("users").document(name)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User successfully updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Update unsuccessful", e);
                    }
                });
    }
}

