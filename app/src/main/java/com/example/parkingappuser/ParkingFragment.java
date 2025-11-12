package com.example.parkingappuser;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParkingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParkingFragment extends Fragment implements WalletFragment.OnBalanceUpdateListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static  String ARG_EMAIL = "emailArg";
    private static  String ARG_BALANCE = "balanceArg";

    private static  String ARG_LICENSE_PLATE = "licensePlateArg";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CustomNumberPicker hoursPicker;
    private CustomNumberPicker minutesPicker;

    String [] minuteArray;

    private TextView costView , timerView;
    private Button showMap , startParking;

    private double totalCost;
    private double costPerHour;

    private CountDownTimer timer;
    private int selectedHours , selectedMinutes;
    private long timeInMs;

    private String userEmail;
    private double userBalance;
    private String locationName , freeStartTime , freeStopTime , freeDays , licensePlateNum;

    private ActivityResultLauncher<Intent> mapLauncher;


    public ParkingFragment() {
        // Required empty public constructor
    }

    public static ParkingFragment newInstance(String email , double balance, String licensePlate) {
        ParkingFragment fragment = new ParkingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        args.putDouble(ARG_BALANCE, balance);
        args.putString(ARG_LICENSE_PLATE, licensePlate);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userEmail = getArguments().getString(ARG_EMAIL, null);
            userBalance = getArguments().getDouble(ARG_BALANCE,0.0);
            licensePlateNum = getArguments().getString(ARG_LICENSE_PLATE, null);
        }
    }

    public void onViewCreated(View view , Bundle savedInstanceState) {
        super.onViewCreated(view , savedInstanceState);
        hoursPicker = view.findViewById(R.id.hourPicker);
        minutesPicker = view.findViewById(R.id.minutePicker);
        showMap = view.findViewById(R.id.chooseLocBtn);
        startParking = view.findViewById(R.id.start_parking_button);
        costView = view.findViewById(R.id.textViewCost);
        timerView = view.findViewById(R.id.textViewTimer);

        minuteArray   = new String[]{"0", "15", "30", "45"};

        hoursPicker.setMinValue(1);
        hoursPicker.setMaxValue(4);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(minuteArray.length - 1);
        minutesPicker.setDisplayedValues(minuteArray);

        timerView.setText("Timer: 00:00:00");
        startParking.setEnabled(false);

        mapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        locationName = data.getStringExtra("locationName");
                        costPerHour = data.getDoubleExtra("costPerHour", 0.0);
                        freeStartTime = data.getStringExtra("freeStartTime");
                        freeStopTime = data.getStringExtra("freeStopTime");
                        freeDays = data.getStringExtra("freeDays");

                        showMap.setText(locationName);
                        checkDateAndTime(startParking);


                        if (costPerHour > 0){
                            startParking.setEnabled(true);
                            updateCost();
                        }

                    }

                }
        );

        hoursPicker.setOnValueChangedListener((picker, oldVal, newVal) -> updateCost());
        minutesPicker.setOnValueChangedListener((picker, oldVal, newVal) -> updateCost());

        updateCost();

        showMap.setOnClickListener(v -> showMap());
        PostData postObj = new PostData();
        startParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!licensePlateNum.isEmpty()) {
                    if (userBalance >= totalCost) {
                        if (startParking.getText().toString().equals(getString(R.string.start_parking))) {
                            timer.start();
                            startParking.setText(getString(R.string.stop_parking));
                            showMap.setEnabled(false);

                            try {
                                postObj.start_parking_session(getString(R.string.StartParkingSession_URL) ,userEmail ,locationName ,licensePlateNum);
//                                System.out.println("Server response: "+msg);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
//                            System.out.println("email = "+userEmail);
//                            System.out.println("location = "+locationName);
//                            System.out.println("plate = "+licensePlateNum);
                        } else {
                            timer.cancel();
                            startParking.setText(getString(R.string.start_parking));
                            timerView.setText("Timer: 00:00:00");
                            Toast.makeText(getContext(), "Πλήρωσες...", Toast.LENGTH_SHORT).show();
                            showMap.setEnabled(true);
                        }
                    } else {
                        Toast.makeText(getContext(), "Ανεπαρκές ποσό πληρωμής!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getContext(), "Δεν όρισες αριθμό πινακίδας!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parking, container, false);
    }

    private void updateCost() {

        selectedHours = hoursPicker.getValue();
        int selectedMinuteIndex = minutesPicker.getValue();

        // Βρίσκουμε την πραγματική τιμή των λεπτών
        selectedMinutes = Integer.parseInt(minuteArray[selectedMinuteIndex]);

        if(selectedMinutes>0){
            timeInMs = (selectedHours * 3600000);

        }
        else{
            timeInMs = (selectedHours * 3600000) + (selectedMinutes * 60000);
        }

        timer = new CountDownTimer(timeInMs, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                timerView.setText("Timer: "+f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
            }
            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                timer.cancel();
                timerView.setText("Timer: 00:00:00");
                showMap.setEnabled(true);

            }


        };


        // Κάνουμε τον σωστό αναλογικό υπολογισμό
        double fractionalHours = selectedMinutes / 60.0;
        double totalHours = selectedHours + fractionalHours;
        totalCost = totalHours * costPerHour;

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

    @Override
    public void onBalanceUpdated(double newBalance) {
        userBalance = newBalance;
    }
}