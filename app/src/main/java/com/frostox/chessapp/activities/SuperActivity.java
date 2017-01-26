package com.frostox.chessapp.activities;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.frostox.chessapp.R;
import com.frostox.chessapp.fragments.ChapterDetailFragment;

import butterknife.Bind;

/**
 * Created by roger on 10/18/2016.
 */
public abstract class SuperActivity extends AppCompatActivity {
    ChapterDetailFragment fragment;

    @Bind(R.id.coord)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.tscore)
    TextView score;

    @Bind(R.id.tremaining)
    TextView remaining;

    public void updateScore(String score){
        this.score.setText(score);
        coordinatorLayout.scrollTo(coordinatorLayout.getLeft(), coordinatorLayout.getTop());
    }


    public void updateRemaining(String remaining){
        this.remaining.setText(remaining);
    }

    public void updateStats(boolean isCorrect){
        fragment.updateStats(isCorrect);
    }

    public void goBack(){
        NavUtils.navigateUpTo(this, new Intent(this, ChapterListActivity.class));
    }

    public ChapterDetailFragment getFragment(){
        return fragment;
    }

    public void setFragment(ChapterDetailFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(coordinatorLayout!=null)
            coordinatorLayout.scrollTo(0, coordinatorLayout.getTop());
    }

}
