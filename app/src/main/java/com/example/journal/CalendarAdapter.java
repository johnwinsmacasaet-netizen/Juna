package com.example.journal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;
    private final LocalDate monthDate;
    private final DatabaseReference calendarRef;

    // ✅ Only one constructor
    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener, LocalDate monthDate) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.monthDate = monthDate;
        this.calendarRef = FirebaseDatabase.getInstance().getReference("calendar");
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);

        // Set cell height
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.1666666);

        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String day = daysOfMonth.get(position);
        holder.dayOfMonth.setText(day);

        if (!day.equals("")) {
            LocalDate date = monthDate.withDayOfMonth(Integer.parseInt(day));

            // Show entry if in the past
            if (date.isBefore(LocalDate.now())) {
                calendarRef.child("entries").child(date.toString()).get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String entry = snapshot.getValue(String.class);
                        holder.dayOfMonth.setText(day + "\n" + entry);
                    }
                });
            }

            // Show reminder if in the future
            if (date.isAfter(LocalDate.now())) {
                calendarRef.child("reminders").child(date.toString()).get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String reminder = snapshot.getValue(String.class);
                        holder.dayOfMonth.setText(day + "\n🔔 " + reminder);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }
}
