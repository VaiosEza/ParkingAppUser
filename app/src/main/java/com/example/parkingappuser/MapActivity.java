package com.example.parkingappuser;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private SearchView mapSearch;
    private Geocoder geocoder;
    private ArrayList <MapLocations> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        RetrieveData locObj = new RetrieveData();


        try {
            locations  = locObj.getLocations(getResources().getString(R.string.GetLocations_URL));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



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
//                    updateMapWithLocation(latLng);

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
        addLocations();
        LatLng greece = new LatLng( 39.4545, 22.6158);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(greece, 6));
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


            }
        });

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                MapLocations markerTag = (MapLocations) marker.getTag();

                String freeTime = "-";
                if (!markerTag.getStartBill().equals("null")){
                    freeTime =  markerTag.getStartBill()+" - "+markerTag.getStopBill();
                }

                new AlertDialog.Builder(MapActivity.this)
                        .setTitle(marker.getTitle())
                        .setMessage("Cost per hour: " + markerTag.getCost() + "€\n" +
                                "Parking Slots: " + markerTag.getSlots()+"\n" +
                                "Free time: "+freeTime+"\n" +
                                "Free days: "+markerTag.getWeekDays())
                        .setPositiveButton("Start Parking", (dialog, which) -> {

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("locationName", markerTag.getLocName());
                            resultIntent.putExtra("costPerHour", markerTag.getCost());
                            resultIntent.putExtra("freeStartTime",markerTag.getStartBill() );
                            resultIntent.putExtra("freeStopTime",markerTag.getStopBill() );
                            resultIntent.putExtra("freeDays",markerTag.getWeekDays() );

                            // ... πρόσθεσε ό,τι άλλο χρειάζεσαι ...
                            setResult(RESULT_OK, resultIntent);
                            finish(); // Κλείσε το MapActivity
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

                return false;
            }
        });
    }

    private void addLocations(){
        for(int i=0 ; i<locations.size();i++){
            MapLocations currentLocation = locations.get(i);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .title(currentLocation.getLocName());

            // Προσθέτουμε το marker στον χάρτη
            Marker marker = gMap.addMarker(markerOptions);
            marker.setTag(currentLocation);


        }
    }

    public void backBtn(View view){
        finish();

   }

}