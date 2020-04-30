package edu.murraystate.androidcamilydashboard.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import edu.murraystate.androidcamilydashboard.adapters.RestaurantAdapter;
import edu.murraystate.androidcamilydashboard.adapters.RestaurantData;
import edu.murraystate.androidcamilydashboard.R;
import edu.murraystate.androidcamilydashboard.utils.SQLiteHelper;
import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class SubActivityFavoriteRestaurant extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, PlacesListener {

    private boolean isLocation = false;
    private List<Marker> previous_marker = null;
    private List<Place> placeList;
    private GoogleMap mMap;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1sec
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5sec

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    // Used to distinguish Permissions requests using "ActivityCompat.requestPermission" from results received from "onRequestPermissionResult".
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    // Define the Permissions required to run the app.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // external save 외부 저장

    private Location mCurrentLocatiion;
    private LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private ArrayList<RestaurantData> restaurantList;
    private ArrayList<RestaurantData> restaurantSearchList;

    private RestaurantAdapter restaurantAdapter;
    private LinearLayout layoutList, layoutMap;
    private ListView restList;
    private ConstraintLayout layoutRestInfo;
    private TextView tvFavorite, tvNearby;
    private TextView tvRestName, tvRestAddr;
    private CheckBox cbFavorite;
    private EditText etSearch;
    private LatLng selectLatLng;
    private String selectedRestName;

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_sub_favorite_restaurant);

        layoutList = findViewById(R.id.layout_list);
        layoutMap = findViewById(R.id.layout_map);
        restList = findViewById(R.id.list_restaurant);
        layoutRestInfo = findViewById(R.id.layout_rest_info);
        tvFavorite = findViewById(R.id.tv_favorite);
        tvNearby = findViewById(R.id.tv_nearby);
        tvRestName = findViewById(R.id.tv_rest_name);
        tvRestAddr = findViewById(R.id.tv_rest_addr);
        mLayout = findViewById(R.id.layout_main);
        cbFavorite = findViewById(R.id.cb_favorite);
        etSearch = findViewById(R.id.et_search);

        restaurantAdapter = new RestaurantAdapter(this);
        restList.setAdapter(restaurantAdapter);

        tvFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutList.setVisibility(View.VISIBLE);
                layoutMap.setVisibility(View.GONE);

                restaurantList = SQLiteHelper.getInstance(SubActivityFavoriteRestaurant.this).selectRestaurantData();
                restaurantSearch(etSearch.getText().toString());
            }
        });

        tvNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutList.setVisibility(View.GONE);
                layoutMap.setVisibility(View.VISIBLE);
            }
        });

        cbFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbFavorite.isChecked()) {
                    RestaurantData restaurantData = new RestaurantData();
                    restaurantData.setRestaurantAddr(tvRestAddr.getText().toString());
                    restaurantData.setRestaurantName(tvRestName.getText().toString());
                    restaurantData.setLat(String.valueOf(selectLatLng.latitude));
                    restaurantData.setLon(String.valueOf(selectLatLng.longitude));
                    SQLiteHelper.getInstance(SubActivityFavoriteRestaurant.this).insertRestaurantData(restaurantData);
                }
                else {
                    SQLiteHelper.getInstance(SubActivityFavoriteRestaurant.this).deleteRestaurantData(tvRestName.getText().toString());
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                restaurantSearch(editable.toString());
            }
        });

        registerForContextMenu(restList);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        restList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RestaurantData restaurantData = (RestaurantData)restaurantAdapter.getItem(i);
                setRestaurantLocation(restaurantData);
            }
        });

        restList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedRestName = ((RestaurantData)restaurantAdapter.getItem(i)).getRestaurantName();
                return false;
            }
        });

        restaurantList = SQLiteHelper.getInstance(this).selectRestaurantData();
        restaurantSearchList = new ArrayList<>();
        restaurantSearchList.addAll(restaurantList);
        restaurantAdapter.setList(restaurantSearchList);
    }

    private void restaurantSearch(String keyword) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        restaurantSearchList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        // Clear the list and create a new one every time you type a character
        if (TextUtils.isEmpty(keyword)) {
            restaurantSearchList.addAll(restaurantList);
        }

        else
        {
            // Search all data in the list.
            for(int i = 0;i < restaurantList.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                // Returns true if all data in arraylist contains the word entered (charText).
                if (restaurantList.get(i).getRestaurantName().toLowerCase().contains(keyword))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    // Add the retrieved data to the list.
                    restaurantSearchList.add(restaurantList.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        // Since the list data has changed, update the adapter to show the retrieved data on the screen.
        restaurantAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        previous_marker = new ArrayList<>();
        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //Before you see the Runtime Permission Request dialog box or the GPS Active Request dialog box,
        setDefaultLocation();

        //런타임 퍼미션 처리 Runtime Permission Processing
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        // 1. Check if user have a location permission.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // 2. If you already have a permission,
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // ( Under Android version 6.0 don't need runtime permission.
            startLocationUpdates(); // 3. 위치 업데이트 시작

        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
                 //2. If user have never allowed a Permissions request, user will need a Permissions request. There are two cases (3-1, 4-1).

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            // 3-1. A user has denied a permission.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                // 3-2. Before proceeding with the request, need to explain to the user why need a permission.
                Snackbar.make(mLayout, "Location access is required to run this app.",
                        Snackbar.LENGTH_INDEFINITE).setAction("Check", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        // 3-3. Requesting a permission to the user. Request results are received from onRequestPermissionResult.
                        ActivityCompat.requestPermissions( SubActivityFavoriteRestaurant.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 4-1. If the user has never rejected a permission, request it immediately.
                // Request results are received from onRequestPermissionResult.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                selectLatLng = marker.getPosition();
                tvRestName.setText(marker.getTitle());
                tvRestAddr.setText(marker.getSnippet());
                layoutRestInfo.setVisibility(View.VISIBLE);

                if (isFavorite(marker.getTitle()))
                    cbFavorite.setChecked(true);
                else
                    cbFavorite.setChecked(false);

                return false;
            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                if (null == currentPosition) {
                    //현재 위치에 마커 생성하고 이동
                    //Create and move markers in their current location
                    currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentPosition);
                    mMap.moveCamera(cameraUpdate);
                    mCurrentLocatiion = location;
                    showPlaceInformation(currentPosition);
                }
            }
        }
    };

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : Don't have a permission");
                return;
            }
            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);
        }
    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {
            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


    public String getCurrentAddress(LatLng latlng) {

        //Geocoder... Convert GPS to Address
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //Network problem
            Toast.makeText(this, "Disable Geocoder service", Toast.LENGTH_LONG).show();
            return "Disable Geocoder service";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "Invalid GPS Coordinate", Toast.LENGTH_LONG).show();
            return "Invalid GPS Coordinate";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "address undiscovered", Toast.LENGTH_LONG).show();
            return "address undiscovered";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "Unable to get location information";
        String markerSnippet = "Check location permission and GPS activation.";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    //From now on, method for using runtime permission.
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }
        return false;
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     * This method returns the results of a Permission request using "ActivityCompat.requestPermission".
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // Check that all permissions are allowed.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                // If you have allowed Permissions, start updating location.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                // If there is a permission that you declined, explain the reason why the app is not available and exit the app.
                // There are two cases.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    // If user select Reject permission, run the app again to select Allow to use it.
                    Snackbar.make(mLayout, "Permission denied. Please run the app again to allow the permission. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("Check", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    // If the user has checked "Don't ask again" and selected Deny.
                    // User must allow a permission in Phone Setup (app information) before use the app.
                    Snackbar.make(mLayout, "Permission denied. You have to allow permission in Phone Setting(app information). ",
                            Snackbar.LENGTH_INDEFINITE).setAction("Check", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //Method for activation GPS
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SubActivityFavoriteRestaurant.this);
        builder.setTitle("Disable Location Services");
        builder.setMessage("Location services are required to use the app..\n"
                + "Would you like to modify the location settings?");
        builder.setCancelable(true);
        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //Validate that the user has enabled GPS
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS already activated.");
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }

    @Override
    public void onPlacesFailure(PlacesException e) {
    }

    @Override
    public void onPlacesStart() {
    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                placeList = places;

                for (noman.googleplaces.Place place : places) {

                    if (!place.getName().contains("'")) {
                        LatLng latLng
                                = new LatLng(place.getLatitude()
                                , place.getLongitude());

                        String markerSnippet = getCurrentAddress(latLng);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(place.getName());
                        markerOptions.snippet(markerSnippet);
                        Marker item = mMap.addMarker(markerOptions);
                        previous_marker.add(item);
                    }
                }

                //Remove duplicate markers
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);
            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }

    private void showPlaceInformation(LatLng location)
    {
        mMap.clear();//지도 클리어 map clear

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어 Local Information Marker Clear

        new NRPlaces.Builder()
                .listener(SubActivityFavoriteRestaurant.this)
                .key(getString(R.string.google_map_key))
                .latlng(location.latitude, location.longitude)//Current Location
                .radius(500) //500 미터 내에서 검색 Search within 500 meters
                .type(PlaceType.RESTAURANT) //Restaurant
                .build()
                .execute();
    }

    private boolean isFavorite(String name) {

        for(int i = 0; i < restaurantList.size(); i++) {
            if (name.equals(restaurantList.get(i).getRestaurantName()))
                return true;
        }
        return false;
    }

    /**
     * 리스트 아이템 롱클릭시 팝업 아이템 정의
     * Define pop-up items when long-clicking list items
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list_restaurant){
            AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo)menuInfo;
            MenuItem mnu1 = menu.add(0,0,0,"Delete");
        }
    }

    /**
     * 팝업 아이템 클릭시 이벤트 정의
     * Define an event when user click a pop-up
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {
            case 0:
                //If pressed Edit, modify the currently selected item.
                SQLiteHelper.getInstance(SubActivityFavoriteRestaurant.this).deleteRestaurantData(selectedRestName);
                restaurantList = SQLiteHelper.getInstance(SubActivityFavoriteRestaurant.this).selectRestaurantData();
                restaurantSearch(etSearch.getText().toString());
                break;
        }
        return true;
    }

    private void setRestaurantLocation(RestaurantData restaurantData) {

        LatLng restPosition = new LatLng(Double.parseDouble(restaurantData.getLat()),
                Double.parseDouble(restaurantData.getLon()));

        selectLatLng = restPosition;
        tvRestName.setText(restaurantData.getRestaurantName());
        tvRestAddr.setText(restaurantData.getRestaurantAddr());
        layoutRestInfo.setVisibility(View.VISIBLE);
        layoutMap.setVisibility(View.VISIBLE);
        layoutList.setVisibility(View.GONE);
        cbFavorite.setChecked(true);


        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(restPosition);
        mMap.moveCamera(cameraUpdate);

        boolean isMarker = false;

        if (null != previous_marker) {

            for (int i = 0; i < previous_marker.size(); i++) {
                if (restaurantData.getRestaurantName().equals(previous_marker.get(i).getTitle())) {
                    previous_marker.get(i).showInfoWindow();
                    isMarker = true;
                }
            }
        }

        if (!isMarker) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(restPosition);
            markerOptions.title(restaurantData.getRestaurantName());
            markerOptions.snippet(restaurantData.getRestaurantAddr());
            Marker marker = mMap.addMarker(markerOptions);
            marker.showInfoWindow();
        }
    }
}