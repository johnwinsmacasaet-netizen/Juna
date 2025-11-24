package com.example.journal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Profile extends AppCompatActivity {

    androidx.constraintlayout.widget.ConstraintLayout notif, calendar, gallery, journal, share, logout;
    androidx.constraintlayout.widget.ConstraintLayout constraintLayout;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Check if ActionBar is available before calling hide()
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        constraintLayout = findViewById(R.id.constraintLayout1);
        notif = findViewById(R.id.notif);
        calendar = findViewById(R.id.calendar);
        gallery = findViewById(R.id.gallery);
        journal = findViewById(R.id.journal);
        share = findViewById(R.id.share);
        logout = findViewById(R.id.logout);

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, home.class);
                startActivity(intent);
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Calendar.class);
                startActivity(intent);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Gallery.class);
                startActivity(intent);
            }
        });

        journal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Journal_Homepage.class);
                startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Diploma Guruvar");
                    i.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?=" + getApplicationContext().getPackageName());
                    startActivity(Intent.createChooser(i, "Share Note with"));
                } catch (Exception e) {
                    Toast.makeText(Profile.this, "Unable to share Note  ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert_dialog = new AlertDialog.Builder(context);
                alert_dialog.setTitle("Log Out...");
                alert_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                alert_dialog.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Profile.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                alert_dialog.show();
            }
        });
    }
}
