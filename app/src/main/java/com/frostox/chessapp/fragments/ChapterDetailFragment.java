package com.frostox.chessapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.frostox.chessapp.R;
import com.frostox.chessapp.activities.ChapterDetailActivity;
import com.frostox.chessapp.activities.ChapterListActivity;
import com.frostox.chessapp.adapters.GridAdapter;
import com.frostox.chessapp.models.Chapter;
import com.frostox.chessapp.models.PGN;
import com.frostox.chessapp.models.User;
import com.frostox.chessapp.util.Util;
import com.frostox.chessapp.wrappers.SQIWrapper;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import chesspresso.game.Game;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.position.Position;


/**
 * A fragment representing a single Chapter detail screen.
 * This fragment is either contained in a {@link ChapterListActivity}
 * in two-pane mode (on tablets) or a {@link ChapterDetailActivity}
 * on handsets.
 */
public class ChapterDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_NAME = "item_name";

    public String userKey;

    /**
     * The dummy content this fragment is presenting.
     */
    private Chapter chapter;
    private List<PGN> pgns;
    Firebase refPGNs;

    private int index = 0;



    Game game;


    GridView gridView;

    RelativeLayout gridViewContainer;

    LinearLayout left, bottom;

    public RelativeLayout getGridViewContainer() {
        return gridViewContainer;
    }

    public GridView getGridView() {
        return gridView;
    }

    GridAdapter adapter;

    Button hint;

    Toast toast;

    TextView toPlay;

    Button back, next, done;
    RelativeLayout hintContainer, reviewContainer;
    boolean reviewMode = false;

    public boolean isReviewMode() {
        return reviewMode;
    }

    private int current = 1;

    Firebase refUser;
    Firebase ref;

    List<String> pgnIds = new ArrayList<>();



    User user;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChapterDetailFragment() {
    }


    public void updateStats(boolean isCorrect){
        if(isCorrect){
            final Firebase correctRef = ref.child("users").child(userKey).child("correct");
            correctRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long score = (Long) dataSnapshot.getValue();
                    if(score==null){
                        correctRef.setValue(1);
                    } else {
                        correctRef.setValue(score + 1);
                    }
                    correctRef.removeEventListener(this);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    correctRef.removeEventListener(this);
                }
            });
        } else {
            final Firebase correctRef = ref.child("users").child(userKey).child("wrong");
            correctRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Long score = (Long) dataSnapshot.getValue();
                    if(score==null){
                        correctRef.setValue(1);
                    } else {
                        correctRef.setValue(score + 1);
                    }
                    correctRef.removeEventListener(this);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    correctRef.removeEventListener(this);
                }
            });
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref = new Firebase("https://blistering-heat-8553.firebaseio.com");

        userKey = getArguments().getString("userKey");
        System.out.println("here >>>>" + userKey);



        toast = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG);

    }

    public void setReviewMode(boolean state){
        reviewMode = state;
        if(state){
            hintContainer.setVisibility(View.GONE);
            reviewContainer.setVisibility(View.VISIBLE);
        } else {
            hintContainer.setVisibility(View.VISIBLE);
            reviewContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chapter_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (chapter != null) {

        }

        back = (Button) rootView.findViewById(R.id.reviewLeft);
        next = (Button) rootView.findViewById(R.id.reviewRight);
        done = (Button) rootView.findViewById(R.id.reviewDone);
        hintContainer = (RelativeLayout) rootView.findViewById(R.id.hintContainer);
        reviewContainer = (RelativeLayout) rootView.findViewById(R.id.reviewContainer);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.stepForward();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.stepBack();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReviewMode(false);
                nextGame(adapter.getScore());
            }
        });

        gridView = (GridView) rootView.findViewById(R.id.board);
        gridViewContainer = (RelativeLayout) rootView.findViewById(R.id.grid_view_container);
        left = (LinearLayout) rootView.findViewById(R.id.left);
        bottom = (LinearLayout) rootView.findViewById(R.id.bottom);
        toPlay = (TextView) rootView.findViewById(R.id.toPlay);
        hint = (Button) rootView.findViewById(R.id.hint);
        hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateHint();
            }
        });
//        List<SQIWrapper> sqis = new ArrayList<>();
//        for(int i=0; i<64; i++)
//            sqis.add(new SQIWrapper(i+1));
//        adapter = new GridAdapter(((ChapterDetailActivity) this.getActivity()), this.getLayoutInflater(savedInstanceState), sqis);
//
        if(!Util.getSetting("coordinates", getActivity().getApplicationContext())){
            left.setVisibility(View.GONE);
            bottom.setVisibility(View.GONE);
        }


        if (getArguments().containsKey(ARG_ITEM_ID) && getArguments().containsKey(ARG_ITEM_NAME)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            pgns = new ArrayList<>();
            refPGNs = new Firebase("https://blistering-heat-8553.firebaseio.com/pgns");
            final Query query = refPGNs.orderByChild("chapter").equalTo(getArguments().getString(ARG_ITEM_ID));

            chapter = new Chapter();
            chapter.setId(getArguments().getString(ARG_ITEM_ID));
            chapter.setChapter(getArguments().getString(ARG_ITEM_NAME));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(chapter.getChapter());
            }

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {


                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        PGN pgn = postSnapshot.getValue(PGN.class);
                        pgn.setId(postSnapshot.getKey());
                        pgns.add(pgn);
                        pgnIds.add(postSnapshot.getKey());
                    }


                    if(!pgns.isEmpty()){
                        setUpGame(pgns.get(current-1));
                        ((ChapterDetailActivity) ChapterDetailFragment.this.getActivity()).updateRemaining(current+"/"+pgns.size());
                    }

                    query.removeEventListener(this);


                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });




        }

        return rootView;
    }

    List<SQIWrapper> sqis = new ArrayList<>();



    public void setUpGame(PGN pgn){
        sqis.clear();
        for(int i=0; i<64; i++)
            sqis.add(new SQIWrapper(i+1));
        adapter = new GridAdapter(((ChapterDetailActivity) this.getActivity()), this.getActivity().getLayoutInflater(), sqis);
        gridView.setAdapter(adapter);

        StringReader sReader = new StringReader(pgn.getContent());
        PGNReader pReader = new PGNReader(sReader, pgn.getId());
        try {
            game = null;
            game = pReader.parseGame();
            adapter.setGame(game);
        } catch (PGNSyntaxError pgnSyntaxError) {
            pgnSyntaxError.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(game == null) return;


        game.goBackToLineBegin();

        Position position = game.getPosition();
        boolean flipped = false;

        if(position.getToPlay() == 0){
            toPlay.setText("White To Play");


            if(Util.getSetting("rotation", getActivity().getApplicationContext())){
                adapter.flipBoard();
                flipped = true;
            }
        } else {
            toPlay.setText("Black To Play");
            if(!Util.getSetting("rotation", getActivity().getApplicationContext())){
                adapter.flipBoard();
                flipped = true;
            }
        }

        if(!flipped)

        for(int i=0; i<64; i++){
            adapter.setPieceToSqi(position.getPiece(i), position.getColor(i), i);

        }

        else

        for(int i=63; i>=0; i--){
            adapter.setPieceToSqi(position.getPiece(i), position.getColor(i), i);

        }

        adapter.notifyDataSetChanged();





    }

    public void loadPgn(String id){
        int index = pgnIds.indexOf(id);
        current = index+1;

        ((ChapterDetailActivity) ChapterDetailFragment.this.getActivity()).updateRemaining((current)+"/"+pgns.size());
        adapter.clearHing();
        setUpGame(pgns.get(current-1));
    }

    public void nextGame(int score){



        Firebase userPgnRef = new Firebase("https://blistering-heat-8553.firebaseio.com/users/" + userKey + "/pgns/" + pgns.get(current-1).getId());

        userPgnRef.setValue(score);
        if(pgns.size()==current){
            new AlertDialog.Builder(this.getActivity())
                    .setTitle("Chapter Complete")
                    .setMessage("Congratulations! You completed " + chapter.getChapter())
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            ((ChapterDetailActivity) getActivity()).finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }else{


            toast.setText("Well done! You scored " + score);
            toast.setGravity(Gravity.CENTER, 0 , 0);
            toast.show();
            ((ChapterDetailActivity) getActivity()).updateScore(0 + "");

            current++;
            ((ChapterDetailActivity) ChapterDetailFragment.this.getActivity()).updateRemaining(current+"/"+pgns.size());
            adapter.clearHing();
            setUpGame(pgns.get(current-1));
        }
    }

    public Button getHint(){
        return hint;
    }




}
