package com.example.journal;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;

public class Calendar extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private RecyclerView calendarRecyclerView;
    private TextView monthYearText;
    private LocalDate selectedDate;
    private DatabaseReference calendarRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar);

        // Get current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user signed in!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Use user-specific node in database
        String uid = currentUser.getUid();
        calendarRef = FirebaseDatabase.getInstance().getReference("calendar")
                .child("users")
                .child(uid);

        initWidgets();

        selectedDate = LocalDate.now();
        setMonthView();
    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, selectedDate);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
            LocalDate clickedDate = selectedDate.withDayOfMonth(Integer.parseInt(dayText));

            if (clickedDate.isBefore(LocalDate.now())) {
                showEntryDialog(clickedDate);
            } else {
                showReminderDialog(clickedDate);
            }
        }
    }

    private void showEntryDialog(LocalDate date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Entry for " + date.toString());

        final EditText input = new EditText(this);
        input.setHint("Write your entry...");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String text = input.getText().toString();
            calendarRef.child("entries").child(date.toString()).setValue(text)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Entry saved!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showReminderDialog(LocalDate date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Reminder for " + date.toString());

        final EditText input = new EditText(this);
        input.setHint("Reminder...");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String text = input.getText().toString();
            calendarRef.child("reminders").child(date.toString()).setValue(text)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Reminder saved!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
