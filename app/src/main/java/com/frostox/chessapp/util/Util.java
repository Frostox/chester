package com.frostox.chessapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.frostox.chessapp.activities.Settings;

/**
 * Created by roger on 6/5/16.
 */
public class Util {
    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Log.d("dimensions", width + ":" + height);
        Bitmap b = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        //v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);

        v.draw(c);
        return b;
    }

    public static boolean getSetting(String settingName, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(Settings.PREFERENCE, Context.MODE_PRIVATE);

        return sharedPref.getBoolean(settingName, false);
    }

    public static void initSettings(Context context){
        String[] settings = {"loadNext",
                "multipleMoves",
                "coordinates",
                "rotation",
                "sound",
                "vibration"};

        SharedPreferences sharedPref = context.getSharedPreferences(Settings.PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(settings[0], sharedPref.getBoolean(settings[0], true));
        editor.putBoolean(settings[1], sharedPref.getBoolean(settings[1], true));
        editor.putBoolean(settings[2], sharedPref.getBoolean(settings[2], true));
        editor.putBoolean(settings[3], sharedPref.getBoolean(settings[3], true));
        editor.putBoolean(settings[4], sharedPref.getBoolean(settings[4], true));
        editor.putBoolean(settings[5], sharedPref.getBoolean(settings[5], true));

        editor.apply();
        editor.commit();



    }
}
