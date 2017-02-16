package com.frostox.chessapp;

import android.app.Application;

import com.frostox.chessapp.util.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by roger on 16/4/16.
 */
public class App extends Application {



    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("chapters");
        scoresRef.keepSynced(true);

        scoresRef = FirebaseDatabase.getInstance().getReference("pgns");
        scoresRef.keepSynced(true);

        scoresRef = FirebaseDatabase.getInstance().getReference("users");
        scoresRef.keepSynced(true);


        Util.initSettings(this);


    }
}
