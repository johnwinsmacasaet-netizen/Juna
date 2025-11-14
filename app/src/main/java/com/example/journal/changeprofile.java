package com.example.journal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.widget.EditText;
import java.util.Calendar;

public class changeprofile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeprofile);

// Inside your Activity's onCreate()
        EditText dateOfBirth = findViewById(R.id.dateOfBirth);

        dateOfBirth.setOnClickListener(v -> {
            // ✅ Create a Calendar instance
            final Calendar calendar = Calendar.getInstance();

            // ✅ Use correct constants with uppercase
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // ✅ Create DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        dateOfBirth.setText(date);
                    },
                    year, month, day
            );

            // Optional: Prevent selecting future dates
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            // Show picker
            datePickerDialog.show();
        });

    }
}