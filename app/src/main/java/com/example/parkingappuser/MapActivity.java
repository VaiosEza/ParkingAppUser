package com.example.parkingappuser;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private SearchView mapSearch;
    private double latitude;
    private double longitude;
    private String roadName;
    private String roadNum;
    private Geocoder geocoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        SupportMapFragment mapFragment =  SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.google_map, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);

        mapSearch = findViewById(R.id.mapSearchView);
        geocoder = new Geocoder(this, new Locale("el", "GR"));
        mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query == null || query.trim().isEmpty()) {
                    return false;
                }

                List<Address> addressList;
                try {
                    addressList = geocoder.getFromLocationName(query, 1);

                    if (addressList == null || addressList.isEmpty()) {
                        Toast.makeText(MapActivity.this, "Η τοποθεσία δεν βρέθηκε.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    updateMapWithLocation(latLng);

                } catch (IOException e) {
                    Toast.makeText(MapActivity.this, "Σφάλμα σύνδεσης!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(MapActivity.this, "Άγνωστο σφάλμα κατά την αναζήτηση!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        LatLng greece = new LatLng( 39.4545, 22.6158);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(greece, 6));
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

                updateMapWithLocation(latLng);


            }
        });
    }

    private void updateMapWithLocation(LatLng latLng) {
        latitude = latLng.latitude;
        longitude = latLng.longitude;

        gMap.clear();
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);

                if (address.getThoroughfare() != null){
                    roadName = address.getThoroughfare(); //όνομα οδού / δρόμου κλπ.
                    roadNum = address.getSubThoroughfare(); //αριθμός οδού
                }
                else{
                    roadName ="Unknown";
                    roadNum = "";
                }


                    markerOptions.title(roadName+" "+roadNum);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        gMap.addMarker(markerOptions);
    }


    public void backBtn(View view){

//        Intent intent = new Intent(this, PostLoc.class);
//        intent.putExtra("latitude", latitude);
//        intent.putExtra("longitude", longitude);
//        intent.putExtra("roadName", roadName);
//        intent.putExtra("roadNum", roadNum);
//        setResult(RESULT_OK, intent);
        finish();

   }

}