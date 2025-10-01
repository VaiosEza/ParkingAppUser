package com.example.parkingappuser;

import android.app.TimePickerDialog;
import android.content.Context;

import java.util.Calendar;
import java.util.Locale;

public class TimePicker {

    private Context context;

    public TimePicker(Context context) {
        this.context = context;
    }

    public void pickTime(TimeCallback callback) {
        Calendar now = Calendar.getInstance();
        int h = now.get(Calendar.HOUR_OF_DAY);
        int m = now.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                context,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                (tp, hourOfDay, minute) -> {
                    String formatted = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    callback.onTimeSelected(formatted); // ✅ καλείται μετά την επιλογή
                },
                h, m,
                true
        );
        dialog.show();
    }
}
