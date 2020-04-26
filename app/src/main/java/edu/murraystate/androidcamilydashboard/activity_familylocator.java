package edu.murraystate.androidcamilydashboard;

import android.content.Intent;
import android.content.IntentSender;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.*;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.ui.IconGenerator;

import java.io.Serializable;
import java.util.HashMap;

public class activity_familylocator extends MainActivity implements OnMapReadyCallback,
         Serializable {

    private static final String LOG_TAG = "ERROR";
    private GoogleMap mMap;
    protected Marker markerSelf;

    private HashMap<String, Contact> contacts = new HashMap<>();

    Button contactButton;

    private static final String TAG = activity_familylocator.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int LAUNCH_CONTACT = 1;

    Bitmap icon;
    TextView markerText;
    IconGenerator iGen;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familylocator);
        setTextView("Test");

        contactButton = (Button) findViewById(R.id.buttonContacts);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFused = LocationServices.getFusedLocationProviderClient(this);
        mFused.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(Location location) {
                current = location;
                LatLng self = new LatLng(location.getLatitude(), location.getLongitude());

                //markerSelf = mMap.addMarker(new MarkerOptions().position(self)
                //.icon(BitmapDescriptorFactory.fromBitmap(icon)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(self, 12.0f));
            }
        });
        settingsClient = LocationServices.getSettingsClient(this);
        createLocationRequest();
        createLocationCallback();
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                System.out.println("Button Clicked");
                Intent intent = new Intent(getApplicationContext(), activity_locator_contact.class);
                intent.putExtra("contact", contacts);
                startActivityForResult(intent, LAUNCH_CONTACT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (requestCode == LAUNCH_CONTACT)
            if (resultCode == RESULT_OK) {
                Contact result = (Contact) i.getParcelableExtra("Selected");
                LatLng contactLocation = new LatLng(result.getLatitude(), result.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(contactLocation, 12.0f));
            }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);

    }

    /** Called when the user clicks a marker. */
   //@Override
   //public boolean onMarkerClick(final Marker marker) {
   //    marker.getId();
   //}


    private void createLocationCallback() {
        locCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locResult) {
                Log.i("Update", "Updating Location");
                super.onLocationResult(locResult);
                current = locResult.getLastLocation();
                updateLocationUI();
            }
        };
    }

    private void updateLocationUI() {
        getContactInformation();
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
                        updateLocationUI();
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
                                    rae.startResolutionForResult(activity_familylocator.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                        }
                        updateLocationUI();
                    }
                });

    }

    private void stopLocationUpdates() {
        mFused.removeLocationUpdates(locCallback);
    }

    @Override
    public void onLocationChanged(Location location) {
        current = location;
        updateLocationUI();
    }

    public void getContactInformation() {
        Log.i(TAG, "a");
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mMap.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId());
                                if (!document.getId().equals(MainActivity.name)) {
                                    if (!contacts.containsKey(document.get("Name")))
                                    {
                                        contacts.put(document.getId(),
                                                new Contact(document.get("Name").toString(),
                                                        document.get("Phone:").toString(),
                                                        document.getDouble("Latitude"),
                                                        document.getDouble("Longitude")));
                                    }
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    LatLng contactLoc = new LatLng(Double.parseDouble(document.get("Latitude").toString()),
                                            Double.parseDouble(document.get("Longitude").toString()));

                                    setTextView(document.get("Name").toString());
                                    mMap.addMarker(new MarkerOptions().position(contactLoc).title(document.get("Name").toString())
                                            .icon(BitmapDescriptorFactory.fromBitmap(icon)));
                                }
                            }
                        }
                        else
                        {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setTextView(String name) {
        markerText = new TextView(activity_familylocator.this);
        markerText.setText(name);
        markerText.setTextSize(16.0f);
        markerText.setBackgroundColor(Color.parseColor("#ffffff"));
        iGen = new IconGenerator(activity_familylocator.this);
        iGen.setBackground(activity_familylocator.this.getDrawable(R.drawable.amu_bubble_mask));
        iGen.setContentView(markerText);
        icon = iGen.makeIcon();
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        stopLocationUpdates();
        finish();
    }

}
