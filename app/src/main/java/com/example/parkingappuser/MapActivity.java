package com.example.parkingappuser;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
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
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

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
        LatLng greece = new LatLng( locations.get(0).getLatitude(), locations.get(0).getLongitude());
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(greece, 15));
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
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));

                String freeTime = "-";
                if (!markerTag.getStartBill().equals("null")){
                    freeTime =  markerTag.getStartBill().substring(0,5)+" - "+markerTag.getStopBill().substring(0,5);
                }

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.location_details_dialog,null);
                TextView title = mView.findViewById(R.id.textViewDialogTitle);
                TextView costPerHourView = mView.findViewById(R.id.textViewCostPerHour);
                TextView parkingSlotsView = mView.findViewById(R.id.textViewParkingSlots);
                TextView freeTimeView = mView.findViewById(R.id.textViewFreeTime);
                TextView freeDaysView = mView.findViewById(R.id.textViewFreeDays);
                Button cancel = mView.findViewById(R.id.buttonCancel);
                Button select = mView.findViewById(R.id.buttonChooseLocation);
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("locationName", markerTag.getLocName());
                        resultIntent.putExtra("costPerHour", markerTag.getCost());
                        resultIntent.putExtra("freeStartTime",markerTag.getStartBill() );
                        resultIntent.putExtra("freeStopTime",markerTag.getStopBill() );
                        resultIntent.putExtra("freeDays",markerTag.getWeekDays() );
                        setResult(RESULT_OK, resultIntent);
                        dialog.dismiss();
                        finish();
                    }
                });

                title.setText(marker.getTitle());
                costPerHourView.setText("Cost per hour: " + markerTag.getCost()+"€");
                parkingSlotsView.setText("Parking Slots: " + markerTag.getSlots());
                freeTimeView.setText("Free time: "+freeTime);
                freeDaysView.setText("Free days: "+markerTag.getWeekDays());

                dialog.show();
                return true;
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