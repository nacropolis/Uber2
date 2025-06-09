package com.example.uber;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class UberApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}