package edu.murraystate.androidcamilydashboard;

import android.content.Context;
import android.content.IntentSender;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.*;

public class activity_familylocator extends MainActivity implements OnMapReadyCallback{


    private static final String LOG_TAG = "ERROR";
    private GoogleMap mMap;
    protected Marker markerSelf;

    private static final String TAG = activity_familylocator.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 0x1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familylocator);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFused = LocationServices.getFusedLocationProviderClient(this);
        mFused.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                current = location;
                LatLng self = new LatLng(location.getLatitude(), location.getLongitude());
                sendToServer(self);
                markerSelf = mMap.addMarker(new MarkerOptions().position(self).title("Bishop"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(self, 12.0f));
            }
        });
        settingsClient = LocationServices.getSettingsClient(this);
        createLocationRequest();
        createLocationCallback();
    }

    private void sendToServer(LatLng self) {
        //
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

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
        if (current != null) {
            LatLng self = new LatLng(current.getLatitude(), current.getLongitude());
            markerSelf.setPosition(self);
        }
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


}
