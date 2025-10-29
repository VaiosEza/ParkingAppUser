package com.example.parkingappuser;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParkingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParkingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CustomNumberPicker hoursPicker;
    private CustomNumberPicker minutesPicker;

    String [] minuteArray;

    private TextView costView;

    private double totalCost;
    private double costPerHour;
    private String locationName , freeStartTime , freeStopTime , freeDays;
    private double hours;
    private double minutes;

    private ActivityResultLauncher<Intent> mapLauncher;


    public ParkingFragment() {
        // Required empty public constructor
    }

    public static ParkingFragment newInstance(String param1, String param2) {
        ParkingFragment fragment = new ParkingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ParkingFragment newInstance() {
        ParkingFragment fragment = new ParkingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void onViewCreated(View view , Bundle savedInstanceState) {
        super.onViewCreated(view , savedInstanceState);
        hoursPicker = view.findViewById(R.id.hourPicker);
        minutesPicker = view.findViewById(R.id.minutePicker);
        Button showMap = view.findViewById(R.id.chooseLocBtn);
        Button startParking = view.findViewById(R.id.start_parking_button);
        costView = view.findViewById(R.id.textViewCost);

        minuteArray   = new String[]{"0", "15", "30", "45"};

        hoursPicker.setMinValue(1);
        hoursPicker.setMaxValue(4);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(minuteArray.length - 1);
        minutesPicker.setDisplayedValues(minuteArray);



        mapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // Το αποτέλεσμα είναι ΟΚ και περιέχει δεδομένα
                        Intent data = result.getData();

                        // Παίρνουμε τις τιμές χρησιμοποιώντας τα ΙΔΙΑ ακριβώς κλειδιά
                        locationName = data.getStringExtra("locationName");
                        costPerHour = data.getDoubleExtra("costPerHour", 0.0); // 0.0 είναι η default τιμή
                        freeStartTime = data.getStringExtra("freeStartTime");
                        freeStopTime = data.getStringExtra("freeStopTime");
                        freeDays = data.getStringExtra("freeDays");

                        checkDateAndTime(startParking);


                        updateCost();
                    }

                }
        );

        hoursPicker.setOnValueChangedListener((picker, oldVal, newVal) -> updateCost());
        minutesPicker.setOnValueChangedListener((picker, oldVal, newVal) -> updateCost());

        updateCost();


//        costView.setText("Cost: "+cost);
        showMap.setOnClickListener(v -> showMap());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parking, container, false);
    }

    private void updateCost() {
        // Αν δεν έχει επιλεγεί ακόμα τοποθεσία, το κόστος είναι 0
        if (costPerHour == 0.0) {
            costView.setText("Cost: 0.00€");
            return;
        }

        int selectedHours = hoursPicker.getValue();
        int selectedMinuteIndex = minutesPicker.getValue();

        // Βρίσκουμε την πραγματική τιμή των λεπτών
        int actualMinutes = Integer.parseInt(minuteArray[selectedMinuteIndex]);

        // Κάνουμε τον σωστό αναλογικό υπολογισμό
        double fractionalHours = actualMinutes / 60.0;
        double totalHours = selectedHours + fractionalHours;
        double totalCost = totalHours * costPerHour;

        // Εμφανίζουμε το αποτέλεσμα με μορφοποίηση 2 δεκαδικών
        costView.setText(String.format(Locale.US, "Cost: %.2f€", totalCost));
    }


    private void checkDateAndTime(Button button) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            if (freeDays.toLowerCase().contains(LocalDate.now().getDayOfWeek().toString().toLowerCase())) {
                button.setEnabled(false);
                button.setText("Free parking on " + LocalDate.now().getDayOfWeek().toString().toLowerCase());
            }
            else if (!freeStartTime.equals("null")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalTime startTime = LocalTime.parse(freeStartTime, formatter);
                LocalTime endTime = LocalTime.parse(freeStopTime, formatter);
                LocalTime currentTime = LocalTime.now();

                if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
                    button.setEnabled(false);
                    button.setText("Free parking until " + endTime);

                }
                else {
                    button.setEnabled(true);
                    button.setText("Start parking");
                    }
            }
            else {
                button.setEnabled(true);
                button.setText("Start parking");
            }


        }
    }

    public void showMap(){
        Intent intent = new Intent(getActivity(), MapActivity.class);
        mapLauncher.launch(intent);

    }

}