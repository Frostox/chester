package com.frostox.chessapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frostox.chessapp.fragments.ChapterDetailFragment;
import com.frostox.chessapp.R;
import com.frostox.chessapp.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * An activity representing a single Chapter detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ChapterListActivity}.
 */
public class ChapterDetailActivity extends AppCompatActivity {

    public static int RESULT = 123;

    @Bind(R.id.tscore)
    TextView score;

    @Bind(R.id.tremaining)
    TextView remaining;



    ChapterDetailFragment fragment;

    public ChapterDetailFragment getFragment(){
        return fragment;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);






        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setTitle(getIntent().getStringExtra(ChapterDetailFragment.ARG_ITEM_NAME));
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //


        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ChapterDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ChapterDetailFragment.ARG_ITEM_ID));
            arguments.putString(ChapterDetailFragment.ARG_ITEM_NAME,
                    getIntent().getStringExtra(ChapterDetailFragment.ARG_ITEM_NAME));
            System.out.println("here >>>" + getIntent().getStringExtra("userKey"));
            arguments.putString("userKey", getIntent().getStringExtra("userKey"));
            fragment = new ChapterDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chapter_detail_container, fragment)
                    .commit();
        }
    }

    public void updateStats(boolean isCorrect){
        fragment.updateStats(isCorrect);
    }

    public void goBack(){
        NavUtils.navigateUpTo(this, new Intent(this, ChapterListActivity.class));
    }

    Random random = new Random();
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            finish();
            return true;
        } else if(id == R.id.action_share_puzzle){
            RelativeLayout layout = fragment.getGridViewContainer();
            Bitmap bitmap = Util.loadBitmapFromView(layout, layout.getWidth(), layout.getMeasuredHeight());
            try {
                File chessAppFolder = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "chessApp");
                if(chessAppFolder.exists()) chessAppFolder.delete();
                chessAppFolder.mkdirs();
                File file = new File(chessAppFolder, "puzzle" + random.nextInt() +  ".png");
                file.mkdirs();
                if (file.exists()) {
                    Log.d("delete", "exists");
                    if (file.delete()) {
                        Log.d("delete", "done");
                    } else {
                        Log.d("delete", "not done");
                    }
                } else Log.d("delete", "doesn't exist");

                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);
                final Intent intent = new Intent(     android.content.Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("image/png");
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else if(id == R.id.list_puzzles){
            Intent intent = new Intent(this, EagleEye.class);
            intent.putExtra(ChapterDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(ChapterDetailFragment.ARG_ITEM_ID));
            intent.putExtra(ChapterDetailFragment.ARG_ITEM_NAME, getIntent().getStringExtra(ChapterDetailFragment.ARG_ITEM_NAME));
            intent.putExtra("userKey", getIntent().getStringExtra("userKey"));
            startActivityForResult(intent, RESULT);
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT){
            if(data!=null){
                String pgnId = data.getStringExtra("pgnId");
                if(pgnId != null) {
                    fragment.loadPgn(pgnId);
                }
                return;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pgn, menu);
        return true;
    }


    public void updateScore(String score){
        this.score.setText(score);
    }


    public void updateRemaining(String remaining){
        this.remaining.setText(remaining);
    }


}
