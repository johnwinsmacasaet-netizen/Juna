package com.example.journal;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReminderActivity extends AppCompatActivity {

    private ListView reminderListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> reminderList;
    private DatabaseReference reminderRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        reminderListView = findViewById(R.id.reminderListView);
        reminderList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reminderList);
        reminderListView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            reminderRef = FirebaseDatabase.getInstance()
                    .getReference("calendar")
                    .child("users")
                    .child(userId)
                    .child("reminders");

            loadReminders();
        }
    }

    private void loadReminders() {
        reminderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                reminderList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String date = data.getKey();
                    String reminderText = data.getValue(String.class);
                    reminderList.add(date + " → " + reminderText);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
