package com.eatuitive.nutrition;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
/* @Created by shehab September --- November 2020*/


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //enable firebase offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
