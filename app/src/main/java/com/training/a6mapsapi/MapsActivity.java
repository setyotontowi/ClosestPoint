package com.training.a6mapsapi;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private LatLng sydney, position;
    private List<Place> places = new ArrayList<>();
    private Place near;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

        // Add a marker in Sydney and move the camera
        sydney = new LatLng(-34, 151);
        position = randomPoint(90, 180, true);
        marker = mMap.addMarker(new MarkerOptions().position(position).title("Marker in Sydney")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

        System.out.println("Position "+ position.toString());

        // Generate 100 random points
        for(int i=0; i<10; i++){
            LatLng point = randomPoint((int) Math.round(position.longitude), (int) Math.round(position.latitude), false);
            double distance = distance(point, position);
            Place place = new Place(point, distance);
            places.add(place);
            mMap.addMarker(new MarkerOptions().position(point).title("Place "+i));
            System.out.println(place.toString());
        }

        // Select the smallest places
        near = places.get(0);
        for(int i=1; i<10; i++){
            if(near.distance > places.get(i).distance) {
                near = places.get(i);
                index = i;
            }
        }

        System.out.println("Yang paling dekat ada di index "+ index+" dengan jarak " + near.distance);

        Polyline line = mMap.addPolyline(new PolylineOptions().add(position, near.point).width(5).color(Color.RED));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                marker.setPosition(latLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                double distance = distance(sydney, latLng);
                System.out.println("Jarak antara Sydney dan point = " +
                        distance + " meter");
            }
        });
    }

    private double distance(LatLng from, LatLng to){
        double R = 6371E3;
        double lat1 = from.latitude * Math.PI /180;
        double lat2 = to.latitude * Math.PI / 180;
        double dellat = (to.latitude - from.latitude) * Math.PI / 180;
        double dellong = (to.longitude - from.longitude) * Math.PI / 180;

        //System.out.println("delta " + dellat + "  "+dellong);
        double a = Math.sin(dellat/2) * Math.sin(dellat/2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dellong/2) * Math.sin(dellong/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        //System.out.println("a "+a );
        //System.out.println("math sqrt  "+Math.sqrt(Math.abs(a)) + " sqrt (1-a) " + Math.sqrt(1-a));
        double d = R * c /1000;
        return d; // in kilometer
    }

    private double getRandomInRange(int from, int to){
        Random rand = new Random();
        return (rand.nextDouble() * (to - from) + from);
    }

    private LatLng randomPoint(int x, int y, boolean init){
        // 10 = radius
        double lat, lng = 0;
        if(init){
            lat = getRandomInRange(-90, 90);
            lng = getRandomInRange(-180, 180);
        }
        else {
            lat = getRandomInRange(y - 1, y + 1);
            lng = getRandomInRange(x - 1, x + 1);
        }
        LatLng point = new LatLng(lat, lng);
        return point;
    }

    public class Place {
        public LatLng point;
        public double distance;
        private Place(LatLng latLng, double distance){
            point = latLng;
            this.distance = distance;
        }
        @NonNull
        @Override
        public String toString() {
            String result = point.toString() + " dist " + distance;
            return result;
        }
    }
}