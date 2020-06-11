package com.example.narva;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ARN.Narva.UnityPlayerActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class Gps extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,TaskLoadedCallback {
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LatLng latLng;
    MarkerOptions lionmarker, markerOptions1,currentmarker;
    private Polyline currentPolyline;
    double latitude, longtitude;
    double end_latitude,end_longtitude;
    TextView textTitle,points;
    DatabaseReference mreference,database,reference;
    String title,status;
    RecyclerView recyclerView;
    private List<PointReader> artistList;
    private PointAdapter adapter;
    String uid;
    int pathindicator, pointsofuser;
    Dialog dialog,dialog2;
    List<Double> latitudearraylist,longtitudearraylist;
    private Context contex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        Intent i = getIntent();
        hideNavigationBan();
        pathindicator =0;
        points = (TextView)findViewById(R.id.points);
        title = i.getStringExtra("title");
        textTitle = findViewById(R.id.tour_text);
        textTitle.setText(title);
        mreference = FirebaseDatabase.getInstance().getReference().child("Tours").child(title).child("MainCoordinates");
        mreference.keepSynced(true);
        reference = FirebaseDatabase.getInstance().getReference().child("Tours").child(title).child("SubCoordinates");
        reference.keepSynced(true);
        mreference.addValueEventListener(valueEventListener);
        recyclerView = (RecyclerView)findViewById(R.id.setpath);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager ilm = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        ilm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(ilm);
        artistList = new ArrayList<>();
        adapter = new PointAdapter(this,artistList);
        recyclerView.setAdapter(adapter);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        DatabaseReference userpoints  = FirebaseDatabase.getInstance().getReference("user").child(uid);
        userpoints.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(title)) {
                    ToursRegister registerDatabase = new ToursRegister("undone");
                    userpoints.child(title).setValue(registerDatabase);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initGoogleAPIClient();
        checkPermissions();
        database = FirebaseDatabase.getInstance().getReference();
        if (user.isAnonymous()) {
            points.setText("0");
        }

        else{
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long user1 = dataSnapshot.child("user").child(uid).child("points").getValue(Long.class);
                    pointsofuser = Math.toIntExact(user1);
                    String user2 = user1.toString().trim();
                    points.setText(user2);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        ImageButton back_button = findViewById(R.id.button_back);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"WARNING: All of your progress would not been save", Toast.LENGTH_LONG).show();
                back_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Back();
                    }
                });

            }
        });
    }
    ValueEventListener valueEventListener = (new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            artistList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PointReader artist = snapshot.getValue(PointReader.class);
                    artistList.add(artist);
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    public void Unity() {
        Intent intent = new Intent(this, UnityPlayerActivity.class);
        startActivity(intent);
    }
    /* Initiate Google API Client  */
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(Gps.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /* Check Location Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(Gps.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            requestLocationPermission();
        else
            showSettingDialog();

    }

    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Gps.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(Gps.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(Gps.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /* Show Location Access Dialog */
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(Gps.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        break;
                }
                break;
        }
    }
    /* Broadcast receiver to check status of GPS */
    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //Main method for GoogleMap to manipulate with information which you see on map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try{
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.arn_map));

        }catch(Resources.NotFoundException e){
            Log.e("Gps","Cant find style");
        }
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
        Canvas canvas1 = new Canvas(bmp);
        Paint color = new Paint();
        color.setTextSize(35);
        color.setColor(Color.BLACK);
        canvas1.drawText("User Name!", 30, 40,color);
        mMap.setMaxZoomPreference(17.0f);
        mreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                latitudearraylist = new ArrayList<>();
                longtitudearraylist = new ArrayList<>();
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    double latitude = child.child("latitude").getValue(Double.class);
                    double longtitude = child.child("longtitude").getValue(Double.class);
                    latitudearraylist.add(latitude);
                    longtitudearraylist.add(longtitude);
                    String name = child.child("name").getValue(String.class);
                    LatLng cod = new LatLng(latitude,longtitude);
                    mMap.addMarker(new MarkerOptions().position(cod).title(name));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    double latitude = dataSnapshot.child("Coordinate1").child("latitude").getValue(Double.class);
                    double longtitude = dataSnapshot.child("Coordinate1").child("longtitude").getValue(Double.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    LatLng cod = new LatLng(latitude,longtitude);
                    lionmarker = new MarkerOptions().position(cod).title(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.MAGENTA);
                polylineOptions.width(10);
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    double latitude = child.child("latitude").getValue(Double.class);
                    double longtitude = child.child("longtitude").getValue(Double.class);
                    polylineOptions.add(new LatLng(latitude,longtitude));
                }
                mMap.addPolyline(polylineOptions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.373062, 28.200594), 15),5000,null);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Location Permission already granted
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        } else {
            //Request Location Permission
            checkLocationPermission();
        }

    }

    //Put information on GoogleMap which we use in omMapReady method.
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    //Each 2-3 seconds method activate to change information on map.
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        TextView textView = findViewById(R.id.meters);
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        dialog = adapter.dialog;
        Button next = dialog.findViewById(R.id.next);

        if(pathindicator+1 == latitudearraylist.size()){
            next.setText("Finish");
        }

        latitude = location.getLatitude();
        longtitude = location.getLongitude();

        if(pathindicator+1 > latitudearraylist.size()){
            Finishtour(dialog);
        }
        else {
            end_latitude = latitudearraylist.get(pathindicator);
            end_longtitude = longtitudearraylist.get(pathindicator);
            //Place current location marker
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            markerOptions1 = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));
            currentmarker = new MarkerOptions().position(new LatLng(end_latitude, end_longtitude));
            new FetchURL(Gps.this).execute(getUrl(markerOptions1.getPosition(), lionmarker.getPosition(), "walking"), "walking");
            float result[] = new float[1];
            Location.distanceBetween(latitude, longtitude, end_latitude, end_longtitude, result);
            Log.d("TAG", "latitude" + end_latitude);
            Log.d("TAG", "longtitude" + end_longtitude);
            Log.d("TAG", "pathindicator" + pathindicator);
            for (int i = 0, n = result.length; i < n; i++) {
                textView.setText((int) result[i] + "m");
                //if(result[i]<=50)
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pathindicator = pathindicator + 1;
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference userpoints  = FirebaseDatabase.getInstance().getReference("user").child(uid).child(title);
                        userpoints.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String status = dataSnapshot.child("tours").getValue(String.class);
                                if(user.isAnonymous()){
                                    pointsofuser = pointsofuser;
                                }
                                else if (status.equals("done")){
                                    pointsofuser = pointsofuser;
                                }
                                else{
                                    pointsofuser = pointsofuser +200;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        points.setText(Integer.toString(pointsofuser));
                        if(pathindicator+1 > latitudearraylist.size()){
                            Finishtour(dialog);
                        }
                        else{
                            currentmarker = new MarkerOptions().position(new LatLng(end_latitude, end_longtitude));
                            recyclerView.scrollToPosition(pathindicator);
                            dialog.dismiss();
                        }
                    }
                });
            }
        }

    }
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_API__key);
        return url;
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Gps.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }
    //If you go to another app on phone then it stop work but not close.
    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        hideNavigationBan();
    }
    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
    public void hideNavigationBan(){
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }
    @Override
    protected void onStart() {
        super.onStart();
        hideNavigationBan();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        hideNavigationBan();
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBan();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hideNavigationBan();
    }
    public void Back(){
        Intent intent = new Intent(this, nav.class);
        startActivity(intent);
    }
    public void Finishtour(Dialog dialog){
        dialog.dismiss();
        dialog.setContentView(R.layout.finish_recycleview);
        dialog.show();
        Button finish = dialog.findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user.isAnonymous()){
                    Back();
                }
                else{
                    DatabaseReference userpoints  = FirebaseDatabase.getInstance().getReference("user").child(uid);
                    userpoints.child(title).child("tours").setValue("done");
                    userpoints.child("points").setValue(pointsofuser);
                    userpoints.keepSynced(true);
                    Back();
                }
            }
        });
    }
}
