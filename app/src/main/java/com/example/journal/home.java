package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {

    ImageView imageView8;
    TextView textView2;
    LinearLayout homebutton, loggbutton, cartBtn, settingbutton;

    RecyclerView recyclerView;
    JournalAdapter adapter;
    List<JournalEntry> journalList;

    DatabaseReference journalRef;
    FirebaseUser currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // --- Your existing navigation setup ---
        imageView8 = findViewById(R.id.imageView8);
        textView2 = findViewById(R.id.textView2);
        homebutton = findViewById(R.id.homebutton);
        loggbutton = findViewById(R.id.loggbutton);
        cartBtn = findViewById(R.id.cartBtn);
        settingbutton = findViewById(R.id.settingbutton);

       /* imageView8.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, .class);
            startActivity(intent);
        });*/

        /*textView2.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, Profile.class);
            startActivity(intent);
        });*/

        homebutton.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, home.class);
            startActivity(intent);
        });

        loggbutton.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, tracker.class);
            startActivity(intent);
        });

        settingbutton.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, Profile.class);
            startActivity(intent);
        });

        CardView reminderCard = findViewById(R.id.reminderCard);
        reminderCard.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, ReminderActivity.class);
            startActivity(intent);
        });

        // --- RecyclerView for Journals ---
        recyclerView = findViewById(R.id.recyclerViewJournal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        journalList = new ArrayList<>();
        adapter = new JournalAdapter(journalList);
        recyclerView.setAdapter(adapter);

        // --- Firebase user-specific reference ---
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Each user will only see their own journals
            journalRef = FirebaseDatabase.getInstance()
                    .getReference("journals")
                    .child("users")
                    .child(userId);

            journalRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    journalList.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        JournalEntry entry = data.getValue(JournalEntry.class);
                        if (entry != null) {
                            journalList.add(entry);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Optional: handle error here
                }
            });
        }
    }
}
