package com.example.journal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    RadioButton terms;
    Button up;
    EditText name, fname, email, pass, repass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        terms = findViewById(R.id.terms);
        up = findViewById(R.id.up);
        name = findViewById(R.id.name);
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

                if(!terms.isChecked()){
                    Toast.makeText(SignUp.this, "Agree to the Terms and Condition", Toast.LENGTH_LONG).show();
                }
                else if(inname.isEmpty() || infname.isEmpty() || inemail.isEmpty() || inpass.isEmpty() || inrepass.isEmpty()){
                    Toast.makeText(SignUp.this, "Please dont leave a blank field", Toast.LENGTH_LONG).show();
                }
                else if(!inpass.equals(inrepass)){
                    Toast.makeText(SignUp.this, "The Passwords does not match", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(SignUp.this, MainActivity.class);
                    Toast.makeText(SignUp.this, "Registration Successful", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            }
        });

    }
}