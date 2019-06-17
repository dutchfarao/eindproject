package com.example.lifter;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //read the liftspots from csv file using csv helper
        InputStream inputStream = getResources().openRawResource(R.raw.liftplekken);
        CSVHelper csvFile = new CSVHelper(inputStream);
        final ArrayList<Liftspot> liftspots;
        liftspots = csvFile.read();
        for(Liftspot liftspot : liftspots) {
            System.out.println(liftspot.getName() + liftspot.getLat() + liftspot.getLon());
            LatLng position = new LatLng(liftspot.getLat(), liftspot.getLon());
            Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(String.valueOf(liftspot.getId())));
            marker.setTag(position);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        }

//        // Add a marker in almelo
//        LatLng position = new LatLng(52.34034900956803, 6.657414436340332);
//        //setTag(position) while adding marker to map.
//        Marker marker = mMap.addMarker(new MarkerOptions().position(position).title("N35 (Henriëtte Roland Holstlaan) x Weezebeeksingel\n"));
//        marker.setTag(position);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
//
//        // Add a marker in Sydney and move the camera
//        LatLng position1 = new LatLng(52.32897791133947, 6.650199294090271);
//        //setTag(position) while adding marker to map.
//        Marker marker1 = mMap.addMarker(new MarkerOptions().position(position1).title("N35 (Henriëtte Roland Holstlaan)\n"));
//        marker1.setTag(position1);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(position1));

        //set listener for marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, LiftActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("liftspots", liftspots);
                args.putString("markerId", marker.getTitle());
                intent.putExtra("BUNDLE",args);
                startActivity(intent);
                //int position = (int)(marker.getTag());
                //Using position get Value from arraylist
                return true;
            }
        });
    }

    //creation of onclick for floating action button
    private class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MapsActivity.this, MyProfileActivity.class);
            startActivity(intent);
        }
    }


}
