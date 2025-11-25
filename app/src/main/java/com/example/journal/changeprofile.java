package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class changeprofile extends AppCompatActivity {
    EditText dateOfBirth, ffname, cpass;
    Button upd;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String fname;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeprofile);

        ffname = findViewById(R.id.ffname);
        cpass = findViewById(R.id.cpass);
        upd = findViewById(R.id.upd);

        fname = getIntent().getStringExtra("fname");
        pass = getIntent().getStringExtra("pass");

        ffname.setText(fname);
        cpass.setText(pass);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfile();
                Intent intent = new Intent(changeprofile.this, home.class);
                startActivity(intent);
            }

            private void changeProfile() {

                final String affname = ffname.getText().toString().trim();
                final String acpass = cpass.getText().toString().trim();

                if (affname.isEmpty() || acpass.isEmpty()) {
                    Toast.makeText(changeprofile.this, "Fill all the empty field", Toast.LENGTH_SHORT).show();
                    return;
                } else if (acpass.equals(pass)) {
                    Toast.makeText(changeprofile.this, "Can't use the same password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Build map correctly
                Map<String, Object> newprofile = new HashMap<>();
                newprofile.put("newName", affname);
                newprofile.put("newPass", acpass);

                // Upload to Firestore
                db.collection("newInfo")
                        .add(newprofile)
                        .addOnSuccessListener(documentReference ->
                                Toast.makeText(changeprofile.this, "Successfully changed profile", Toast.LENGTH_LONG).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(changeprofile.this, "Changes Failed", Toast.LENGTH_LONG).show()
                        );
            }

        });

    }
}

