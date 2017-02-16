package com.frostox.chessapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.frostox.chessapp.fragments.ChapterDetailFragment;
import com.frostox.chessapp.R;
import com.frostox.chessapp.models.Chapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Chapters. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ChapterDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ChapterListActivity extends SuperActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    FirebaseDatabase database;

    DatabaseReference refChapters;

    DatabaseReference ref;
    List<Chapter> chapters;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());



        final View recyclerView = findViewById(R.id.chapter_list);
        assert recyclerView != null;



        if (findViewById(R.id.chapter_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        chapters = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Query query = ref.child("users").orderByChild("uid").equalTo(user.getUid());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {


                            for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                if((boolean)(postSnapshot.child("blocked").getValue())){
                                    Toast.makeText(ChapterListActivity.this, "Sorry, But you are blocked!", Toast.LENGTH_LONG).show();
                                    mAuth.signOut();

                                    Intent i = new Intent(ChapterListActivity.this, LoginActivity.class);
                                    ChapterListActivity.this.startActivity(i);

                                } else {

                                }

                            }


                        }
                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });
                }
            }
        };

        database = FirebaseDatabase.getInstance();

        ref = database.getReference();

        refChapters = ref.child("chapters");



        refChapters.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                System.out.println("There are " + snapshot.getChildrenCount() + " blog posts");
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Chapter chapter = postSnapshot.getValue(Chapter.class);
                    chapter.setId(postSnapshot.getKey());
                    chapters.add(chapter);
                }

                setupRecyclerView((RecyclerView) recyclerView);
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(chapters));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Chapter> mValues;


        public SimpleItemRecyclerViewAdapter(List<Chapter> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chapter_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).getChapter());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ChapterDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        arguments.putString(ChapterDetailFragment.ARG_ITEM_NAME, holder.mItem.getChapter());
                        arguments.putString("userKey", ChapterListActivity.this.getIntent().getStringExtra("userKey"));

                        fragment = new ChapterDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.chapter_detail_container, fragment)
                                .commit();

                        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coord);
                        score = (TextView) findViewById(R.id.tscore);
                        remaining = (TextView) findViewById(R.id.tremaining);
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ChapterDetailActivity.class);
                        intent.putExtra(ChapterDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        intent.putExtra(ChapterDetailFragment.ARG_ITEM_NAME, holder.mItem.getChapter());
                        intent.putExtra("userKey", getIntent().getStringExtra("userKey"));
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public Chapter mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chapter_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_logout:
                mAuth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, Settings.class);
                startActivity(settingsIntent);
                break;
            case R.id.action_score:
                Intent scoreIntent = new Intent(this, ScoreActivity.class);
                scoreIntent.putExtra("userKey", getIntent().getStringExtra("userKey"));
                startActivity(scoreIntent);

        }

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    




}
