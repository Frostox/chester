package com.frostox.chessapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.frostox.chessapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Settings extends AppCompatActivity {

    @Bind(R.id.settingsList)
    ListView settings;

    List<Setting> settingList = new ArrayList<>();
    SettingsAdapter settingAdapter;

    SharedPreferences sharedPref;

    public static final String PREFERENCE = "settings_preference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = getApplication().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);


        settingList.add(new Setting(1, "loadNext", true, true, sharedPref.getBoolean("loadNext", true), "Load next puzzle automatically", "When unchecked, you will be able to review current puzzle before going to the next"));
        settingList.add(new Setting(2, "multipleMoves", true, true, sharedPref.getBoolean("multipleMoves", true), "Solve Multiple Moves", "When unchecked, multiple moves puzzles will be disabled"));
        settingList.add(new Setting(3, "coordinates", true, false, sharedPref.getBoolean("coordinates", true), "Show Coordinates", ""));
        settingList.add(new Setting(4, "rotation", true, true, sharedPref.getBoolean("rotation", true), "Board Rotation", "Player side on bottom"));
        settingList.add(new Setting(5, "sound", true, true, sharedPref.getBoolean("sound", true), "Sound", "Sound is enabled"));
        settingList.add(new Setting(6, "vibration", true, true, sharedPref.getBoolean("vibration", true), "Vibration", "Vibration is enabled"));

        settingAdapter = new SettingsAdapter(this, R.layout.setting_item, settingList);
        settings.setAdapter(settingAdapter);




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    class Setting {

        final int id;

        boolean checkable;
        boolean subMessageVisible;

        final String settingName;
        String message;
        String subMessage;

        public Setting(int id, String settingName, boolean checkable, boolean subMessageVisible, boolean checked, String message, String subMessage) {
            this.checkable = checkable;
            this.subMessageVisible = subMessageVisible;
            this.message = message;
            this.subMessage = subMessage;
            this.id = id;
            this.settingName = settingName;

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getSettingName(), checked);
            editor.apply();
            editor.commit();
        }

        public String getSettingName() {
            return settingName;
        }

        public boolean isChecked() {
            return sharedPref.getBoolean(getSettingName(), false);
        }

        public void setChecked(boolean checked) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getSettingName(), checked);
            editor.apply();
            editor.commit();
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSubMessage() {
            return subMessage;
        }

        public void setSubMessage(String subMessage) {
            this.subMessage = subMessage;
        }

        public boolean isCheckable() {
            return checkable;
        }

        public void setCheckable(boolean checkable) {
            this.checkable = checkable;
        }

        public boolean isSubMessageVisible() {
            return subMessageVisible;
        }

        public void setSubMessageVisible(boolean subMessageVisible) {
            this.subMessageVisible = subMessageVisible;
        }
    }

    class SettingsAdapter extends ArrayAdapter<Setting>{

        List<Setting> settingList;
        int resourceId;

        public SettingsAdapter(Context context, int resource, List<Setting> objects) {
            super(context, resource, objects);
            settingList = objects;
            resourceId = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            
            final Setting setting = settingList.get(position);

            if(convertView == null){
                convertView = getLayoutInflater().inflate(resourceId, null);
            }


            TextView message, subMessage;
            message = (TextView) convertView.findViewById(R.id.textView);
            subMessage = (TextView) convertView.findViewById(R.id.textView2);
            final Switch check = (Switch) convertView.findViewById(R.id.switch1);

            if(setting.isCheckable()){
                check.setVisibility(View.VISIBLE);
            } else check.setVisibility(View.INVISIBLE);

            if(setting.isSubMessageVisible()){
                subMessage.setVisibility(View.VISIBLE);
            } else subMessage.setVisibility(View.INVISIBLE);

            check.setChecked(setting.isChecked());

            message.setText(setting.getMessage());
            subMessage.setText(setting.getSubMessage());

            switch(setting.id){
                case 4:
                    if(setting.isChecked()){
                        subMessage.setText("Player side on bottom");
                    } else {
                        subMessage.setText("Player side on top");
                    }
                    break;
                case 5:
                    if(setting.isChecked()){
                        subMessage.setText("Sound is enabled");
                    } else {
                        subMessage.setText("Sound is disabled");
                    }
                    break;
                case 6:
                    if(setting.isChecked()){
                        subMessage.setText("Vibration is disabled");
                    } else {
                        subMessage.setText("Vibration is disabled");
                    }
            }

            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    setting.setChecked(check.isChecked());
                    Log.d("changed", setting.getSettingName());
                    editor.putBoolean(setting.getSettingName(), check.isChecked());
                    editor.apply();
                    editor.commit();
                    settingAdapter.notifyDataSetChanged();
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }



}
