package edu.murraystate.androidcamilydashboard.activity;

import android.content.Intent;
import android.content.IntentSender;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.*;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.ui.IconGenerator;

import java.io.Serializable;
import java.util.HashMap;

import edu.murraystate.androidcamilydashboard.MainActivity;
import edu.murraystate.androidcamilydashboard.R;

public class activity_familylocator extends MainActivity implements OnMapReadyCallback, Serializable {

    private static final String LOG_TAG = "ERROR";
    private GoogleMap mMap;
    private HashMap<String, Contact> contacts = new HashMap<>();

    Button contactButton;
    Button dialerButton;
    private String currentPhone;

    private static final String TAG = activity_familylocator.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int LAUNCH_CONTACT = 1;

    Bitmap icon;
    TextView markerText;
    IconGenerator iGen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familylocator);

        contactButton = (Button) findViewById(R.id.buttonContacts);
        dialerButton = (Button) findViewById(R.id.buttonSendToDialer);
        dialerButton.setEnabled(false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFused = LocationServices.getFusedLocationProviderClient(this);
        mFused.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                current = location;
                LatLng self = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(self, 14.0f));
            }
        });
        settingsClient = LocationServices.getSettingsClient(this);
        createLocationRequest();
        createLocationCallback();

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), activity_locator_contact.class);
                intent.putExtra("contact", contacts);
                startActivityForResult(intent, LAUNCH_CONTACT);
            }
        });

        // Sends the current phone number to the dialer
        dialerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(currentPhone));
                startActivity(intent);
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(contactLocation, 14.0f));
            }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true); //show own location

        // Custom marker selection behavior
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Move to the marker location
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(marker.getPosition())
                        .zoom(mMap.getCameraPosition().zoom)
                        .build();
                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.animateCamera(cu);

                // enable the dialer button and store the selected user's phone number
                dialerButton.setText("Dialer");
                Contact contact = contacts.get(marker.getSnippet());
                currentPhone = contact.getPhone();
                dialerButton.setEnabled(true);
                return true;
            }
        });
    }

    // needed for location services
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

    // update location data
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

    // begin the location updates
    private void startLocationUpdates() {
        settingsClient.checkLocationSettings(locSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locSettingsResponse) {
                        HandlerThread handler = new HandlerThread("Location Updates"); // use a handler thread
                        Looper looper = handler.getLooper();
                        mFused.requestLocationUpdates(locRequest,
                                locCallback,
                                looper);
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

    // update location and ui
    @Override
    public void onLocationChanged(Location location) {
        current = location;
        updateLocationUI();
    }

    // Grab contact information from server
    public void getContactInformation() {
        Log.i(TAG, "Retrieving Contact Information...");
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mMap.clear(); // clear markers
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.getId().equals(MainActivity.name)) {
                                    //grab information of each user and assign them to a hashmap (except self)
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
                                    // create marker with custom icon
                                    setCustomIcon(document.get("Name").toString());
                                    mMap.addMarker(new MarkerOptions().position(contactLoc)
                                            .title(document.get("Name").toString())
                                            .snippet(document.getId())
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

    // create custom icon for marker that includes text
    public void setCustomIcon(String name) {
        markerText = new TextView(activity_familylocator.this);
        markerText.setText(name);
        markerText.setTextSize(18.0f);
        markerText.setBackgroundColor(Color.parseColor("#ffffff"));
        iGen = new IconGenerator(activity_familylocator.this);
        iGen.setBackground(activity_familylocator.this.getDrawable(R.drawable.amu_bubble_mask));
        iGen.setContentView(markerText);
        icon = iGen.makeIcon();
    }

    @Override
    public void onBackPressed() // this is to stop reading from the database when backing out of activity
    {
        super.onBackPressed();
        stopLocationUpdates();
        finish();
    }
}
