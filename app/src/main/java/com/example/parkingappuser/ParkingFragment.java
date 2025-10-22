package com.example.parkingappuser;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

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

    private double cost;

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
        CustomNumberPicker hours = view.findViewById(R.id.hourPicker);
        CustomNumberPicker minutes = view.findViewById(R.id.minutePicker);

        String [] minuteArray  = new String[]{"0", "15", "30", "45"};

        hours.setMinValue(1);
        hours.setMaxValue(4);
        minutes.setMinValue(0);
        minutes.setMaxValue(minuteArray.length - 1);
        minutes.setDisplayedValues(minuteArray);

        Button showMap = view.findViewById(R.id.chooseLocBtn);
        TextView costView = view.findViewById(R.id.textViewCost);

        mapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                }
        );
//        costView.setText("Cost: "+cost);
        showMap.setOnClickListener(v -> showMap());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parking, container, false);
    }

    public void showMap(){
        Intent intent = new Intent(getActivity(), MapActivity.class);
        mapLauncher.launch(intent);

    }

}