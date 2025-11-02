package com.example.journal;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.jakewharton.threetenabp.AndroidThreeTen;


public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        AndroidThreeTen.init(this);
    }
}
