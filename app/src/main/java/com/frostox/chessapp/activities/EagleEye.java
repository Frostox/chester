package com.frostox.chessapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.frostox.chessapp.fragments.ChapterDetailFragment;
import com.frostox.chessapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EagleEye extends AppCompatActivity {

    private DatabaseReference ref, refUserPgns;
    private FirebaseDatabase database;

    private List<String> playedPgns;
    private List<String> allPgns;

    @Bind(R.id.eagle_eye)
    GridView gridView;

    GridAdapter gridAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eagle_eye);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        playedPgns = new ArrayList<>();
        allPgns = new ArrayList<>();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra(ChapterDetailFragment.ARG_ITEM_NAME));

        database = FirebaseDatabase.getInstance();

        ref = database.getReference("pgns");
        refUserPgns = database.getReference("users").child(getIntent().getStringExtra("userKey")).child("pgns");


        refUserPgns.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    playedPgns.add(snapshot.getKey());
                }
                refUserPgns.removeEventListener(this);
                Log.d("dataSnapshot", dataSnapshot.getChildrenCount() + "");

                ref.orderByChild("chapter").equalTo(getIntent().getStringExtra(ChapterDetailFragment.ARG_ITEM_ID)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            allPgns.add(snapshot.getKey());
                        }
                        Log.d("dataSnapshot all", dataSnapshot.getChildrenCount() + "");

                        gridAdapter = new GridAdapter(EagleEye.this, R.layout.grid_item, allPgns);
                        gridView.setAdapter(gridAdapter);

                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent();
                                intent.putExtra("pgnId", allPgns.get(position));
                                setResult(ChapterDetailActivity.RESULT, intent);
                                finish();
                            }
                        });


                        ref.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    class GridAdapter extends ArrayAdapter<String>{

        int resourceId;
        List<String> allPgns;

        public GridAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            resourceId = resource;
            allPgns = objects;
        }

        @Override
        public int getCount() {
            return allPgns.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = getLayoutInflater().inflate(resourceId, null);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.grid_text);
            textView.setText("" + (position + 1));

            if(playedPgns.contains(allPgns.get(position))){
                textView.setBackgroundColor(getResources().getColor(R.color.board_black_side));
                textView.setTextColor(getResources().getColor(R.color.white));
            } else {
                textView.setBackgroundColor(getResources().getColor(R.color.black_bg));
                textView.setTextColor(getResources().getColor(R.color.white));
            }

            return convertView;
        }
    }
}
