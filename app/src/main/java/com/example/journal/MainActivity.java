package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    Button signup, loginbutton;
    EditText user, password;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        signup = findViewById(R.id.signup);
        loginbutton = findViewById(R.id.loginbutton);
        user = findViewById(R.id.user);
        password = findViewById(R.id.password);


        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inuser = user.getText().toString().trim();
                String inpassword = password.getText().toString().trim();

                if (inuser.isEmpty() || inpassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill up all fields.", Toast.LENGTH_LONG).show();
                } else {
                    loginWithUsername(inuser, inpassword);
                }
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void loginWithUsername(String username, String password) {

        db.collection("usernames").document(username).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        if (email != null) {
                            // Sign in with email and password
                            mAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.this, "Logged in successfully!", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(MainActivity.this, home.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "Email not found for this username", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Username not found", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
