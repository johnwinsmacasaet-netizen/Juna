package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    RadioButton terms;
    Button up;
    EditText name, fname, email, pass, repass;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views

        fname = findViewById(R.id.fname);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        repass = findViewById(R.id.repass);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inname = name.getText().toString().trim();
                String infname = fname.getText().toString().trim();
                String inemail = email.getText().toString().trim();
                String inpass = pass.getText().toString().trim();
                String inrepass = repass.getText().toString().trim();

                // Validate fields
                if (!terms.isChecked()) {
                    Toast.makeText(SignUp.this, "Agree to the Terms and Conditions", Toast.LENGTH_LONG).show();
                } else if (inname.isEmpty() || infname.isEmpty() || inemail.isEmpty() || inpass.isEmpty() || inrepass.isEmpty()) {
                    Toast.makeText(SignUp.this, "Please don't leave a blank field", Toast.LENGTH_LONG).show();
                } else if (!inpass.equals(inrepass)) {
                    Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                } else {
                    // Create user with Firebase Auth
                    mAuth.createUserWithEmailAndPassword(inemail, inpass)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Save username → email mapping in Firestore
                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put("email", inemail);

                                        db.collection("usernames")
                                                .document(inname) // use username as document ID
                                                .set(userMap)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(SignUp.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                                    // Redirect to login page
                                                    Intent intent = new Intent(SignUp.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(SignUp.this, "Failed to save username: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                });

                                    } else {
                                        Toast.makeText(SignUp.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
