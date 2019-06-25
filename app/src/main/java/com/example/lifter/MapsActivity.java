package com.example.lifter;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LiftspotDownloader.Callback {

    private GoogleMap mMap;
    User userProfile;
    String markerId;
    Liftspot liftspot;
    ArrayList<Liftspot> retrievedLiftspots;
    int counter;
    private static final String TAG = "Mapsactivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        userProfile = (User)intent.getSerializableExtra("userObject");
        //liftspots = (ArrayList<Liftspot>) intent.getSerializableExtra("liftspots");

        // get liftspots
        retrievedLiftspots = new ArrayList<>();


        InputStream inputStream = getResources().openRawResource(R.raw.liftplekken);
        CSVHelper csvFile = new CSVHelper(inputStream);
        final ArrayList<Liftspot> liftspots;
        liftspots = csvFile.read();
        int counter = 0;
        for (counter = 0; counter < liftspots.size(); counter ++) {
            LiftspotDownloader download = new LiftspotDownloader(this);
            download.getLiftspots(this, counter);

        }
        //Log.d(TAG, "retreived liftspots maps:" + retrievedLiftspots.get(0).getName());




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //create listener for floating action button
        FloatingActionButton btn = findViewById(R.id.MapsFloatingActionButton);
        onClick onButtonClick = new onClick();
        btn.setOnClickListener(onButtonClick);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //creation of onclick for floating action button
    private class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MapsActivity.this, MyProfileActivity.class);
            intent.putExtra("userObject", userProfile);
            startActivity(intent);
        }
    }

    @Override
    public void gotLiftspots(Liftspot liftspot1) {
        liftspot = liftspot1;
        Log.d(TAG, "this is the liftspot object: " + liftspot);

        retrievedLiftspots.add(liftspot);
        Log.d(TAG, "this is the retrievedLiftspots arraylist: " + retrievedLiftspots);

        System.out.println(liftspot.getName() + liftspot.getLat() + liftspot.getLon() + liftspot.getId());
        LatLng position = new LatLng(liftspot.getLat(), liftspot.getLon());
        Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(String.valueOf(liftspot.getName())));
        marker.setTag(position);


    }

    @Override
    public void gotLiftspotsError(String message) {

        // Informs the user if an error occurred while logging in
        Toast.makeText(this , "Something went wrong, couldn't load liftspots", Toast.LENGTH_LONG).show();

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "dit is retrievedLiftspots in the onMapReady" + retrievedLiftspots);
        mMap = googleMap;


        //set listener for marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, LiftActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("liftspots", retrievedLiftspots);
                markerId = marker.getTitle();
                args.putString("markerId", markerId);
                args.putSerializable("userObject", userProfile);
                intent.putExtra("BUNDLE",args);
                startActivity(intent);
                //int position = (int)(marker.getTag());
                //Using position get Value from arraylist
                return true;
            }
        });

        LatLng center = new LatLng(52.18, 5.70);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(center).zoom(7).bearing(0).tilt(0).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }



}
