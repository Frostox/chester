package com.frostox.chessapp;

import android.app.Application;

import com.firebase.client.Firebase;
import com.frostox.chessapp.util.Util;

/**
 * Created by roger on 16/4/16.
 */
public class App extends Application {



    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        Firebase.getDefaultConfig().setPersistenceCacheSizeBytes(104857600);

        Firebase refChapters = new Firebase("https://blistering-heat-8553.firebaseio.com/chapters");
        Firebase refPGNs = new Firebase("https://blistering-heat-8553.firebaseio.com/pgns");
        Firebase refUserss = new Firebase("https://blistering-heat-8553.firebaseio.com/users");

        refChapters.keepSynced(true);
        refPGNs.keepSynced(true);
        refUserss.keepSynced(true);

        Util.initSettings(this);


    }
}
