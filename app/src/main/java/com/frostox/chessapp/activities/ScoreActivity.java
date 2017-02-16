package com.frostox.chessapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.frostox.chessapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScoreActivity extends AppCompatActivity {

    @Bind(R.id.grade)
    TextView grade;

    @Bind(R.id.attempted)
    TextView attempted;

    @Bind(R.id.score)
    TextView score;

    @Bind(R.id.accuracy)
    TextView accuracy;

    String gradeText = "", attemptedText = "", scoreText = "", accuracyText = "";

    String[] grades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D"};

    Random random;

    DatabaseReference ref;

    int initialCount = 20;
    int finalCount = 4;
    boolean loaded = false;

    public void randomize(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(loaded){
                    if(initialCount!=0){
                        initialCount--;
                        grade.setText(grades[random.nextInt(grades.length)]);
                        attempted.setText(random.nextInt(100) + "");
                        score.setText(random.nextInt(100) + "");
                        accuracy.setText(random.nextInt(100) + "");
                        randomize();
                    } else if(finalCount!=0){
                        switch (finalCount){
                            case 4:
                                grade.setText(gradeText);
                                attempted.setText(random.nextInt(100) + "");
                                score.setText(random.nextInt(100) + "");
                                accuracy.setText(random.nextInt(100) + "");
                                break;
                            case 3:
                                attempted.setText(attemptedText);
                                score.setText(random.nextInt(100) + "");
                                accuracy.setText(random.nextInt(100) + "");
                                break;
                            case 2:
                                score.setText(scoreText);
                                accuracy.setText(random.nextInt(100) + "");
                                break;
                            case 1:
                                accuracy.setText(accuracyText);
                                break;
                        }
                        finalCount--;
                        randomize();
                    }
                } else {
                    grade.setText(grades[random.nextInt(grades.length)]);
                    attempted.setText(random.nextInt(100) + "");
                    score.setText(random.nextInt(100) + "");
                    accuracy.setText(random.nextInt(100) + "");
                    randomize();
                }

            }
        }, 50);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        random = new Random();

        randomize();

        String userKey = getIntent().getStringExtra("userKey");
        ref = FirebaseDatabase.getInstance().getReference("users").child(userKey);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Long correct, wrong, score = 0L;
                correct = (Long) dataSnapshot.child("correct").getValue();
                wrong = (Long) dataSnapshot.child(("wrong")).getValue();
                if(correct == null) correct = 0L;
                if(wrong == null) wrong = 0L;

                for(DataSnapshot snapshot : dataSnapshot.child("pgns").getChildren()){
                    score += (Long) snapshot.getValue();
                }

                attemptedText = (correct + wrong) + "";
                scoreText = score + "";
                float accuracyPerc = 0;
                if((wrong + correct) != 0)
                accuracyPerc = ((float) correct)/((float) (correct + wrong)) * 100f;
                accuracyText = (int)accuracyPerc + "";

                if(accuracyPerc < 50){
                    gradeText = grades[9];
                } else if(accuracyPerc < 60) {
                    gradeText = grades[8];
                } else if(accuracyPerc < 65) {
                    gradeText = grades[7];
                } else if(accuracyPerc < 70) {
                    gradeText = grades[6];
                } else if(accuracyPerc < 75) {
                    gradeText = grades[5];
                } else if(accuracyPerc < 80) {
                    gradeText = grades[4];
                } else if(accuracyPerc < 85) {
                    gradeText = grades[3];
                } else if(accuracyPerc < 90) {
                    gradeText = grades[2];
                } else if(accuracyPerc < 95) {
                    gradeText = grades[1];
                } else {
                    gradeText = grades[0];
                }

                loaded = true;

                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                ref.removeEventListener(this);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
