package com.frostox.chessapp.adapters;

import android.content.ClipData;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.frostox.chessapp.activities.ChapterDetailActivity;
import com.frostox.chessapp.R;
import com.frostox.chessapp.util.Util;
import com.frostox.chessapp.wrappers.SQIWrapper;

import java.util.ArrayList;
import java.util.List;

import chesspresso.Chess;
import chesspresso.game.Game;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;

/**
 * Created by roger on 13/4/16.
 */

public class GridAdapter extends BaseAdapter {

    private ArrayList<Integer> validMoves = new ArrayList<>();


    private ChapterDetailActivity activity;

    private int flipped = 0;

    private LayoutInflater layoutInflater;

    boolean containsDragable = true;

    private Game game;

    private int fromSqi;

    private int score = 0;

    public int getScore() {
        return score;
    }

    SoundPool sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

    int soundIds[] = new int[3];

    Snackbar snackBar;

    private List<SQIWrapper> sqis;

    public void flipBoard(){
        if(flipped == 0) flipped = 63;
        else flipped = 0;
    }


    public void setGame(Game game){
        this.game = game;
    }

    public void setPieceToSqi(Integer piece, int color, int index){
        sqis.get(index).setPiece(piece);
        sqis.get(index).setColor(color);
    }



    // Gets the context so it can be used later
    public GridAdapter(ChapterDetailActivity activity, LayoutInflater inflater, List<SQIWrapper> sqis) {
        this.activity = activity;
        layoutInflater = inflater;
        this.sqis = sqis;
        snackBar = Snackbar.make(activity.findViewById(R.id.coord), "", Snackbar.LENGTH_LONG);

        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundIds[0] = sp.load(activity, R.raw.failed, 1);
        soundIds[1] = sp.load(activity, R.raw.success, 1);
    }

    // Total number of things contained within the adapter
    public int getCount() {
        if(sqis != null)
            return sqis.size();
        else return 0;
    }

    // Require for structure, not really used in my code.
    public Object getItem(int position) {
        if(sqis != null)
            return sqis.get(position);
        else return null;
    }

    // Require for structure, not really used in my code. Can
    // be used to get the id of an item in the adapter for
    // manual control.
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position,
                        View convertView, ViewGroup parent) {

        final SQIWrapper sqi = sqis.get(Math.abs(flipped-position));

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
           convertView = layoutInflater.inflate(R.layout.piece, null);
        }

        if(sqi.isBlack())
            convertView.setBackgroundColor(activity.getResources().getColor(R.color.black_bg));
        else convertView.setBackgroundColor(activity.getResources().getColor(R.color.white));

        if(sqi.isHighLighted()){
            convertView.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }

        final ImageView piece = (ImageView) convertView.findViewById(R.id.piece);

        if(sqi.getPiece() == null){

        } else {
            setPiece(sqi.getPiece(), sqi.getColor(), piece);
        }

        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN && game!=null && game.getPosition().getPiece(Math.abs(flipped-position))!=0) {

                    if((game.getPosition().getToPlay() != game.getPosition().getColor(Math.abs(flipped-position))) || (activity.getFragment().isReviewMode()) )
                    //if(!((game.getPosition().getColor(Math.abs(flipped-position)) == 1 && flipped == 0) || (game.getPosition().getColor(Math.abs(flipped-position)) == 0 && flipped == 63))){
                        return false;
                    //}



                    fromSqi = Math.abs(flipped-position);


                    short[] moves = game.getPosition().getAllMoves();
                    validMoves.clear();

                    for(short move:moves){
                        try {
                            game.getPosition().doMove(move);
                            Move mv = game.getPosition().getLastMove();

                            if (mv.getFromSqi() == Math.abs(flipped-position)) {
                                validMoves.add(mv.getToSqi());
                            }

                            game.getPosition().undoMove();
                        } catch (IllegalMoveException e) {
                            e.printStackTrace();
                        }

                    }

                    containsDragable = true;
                    ClipData clipData = ClipData.newPlainText("", "");
                    View.DragShadowBuilder dsb = new View.DragShadowBuilder(piece);
                    piece.startDrag(clipData, dsb, piece, 0);
                    piece.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return false;
                }

            }
        });

        convertView.setOnDragListener(new View.OnDragListener() {


            @Override
            public boolean onDrag(View v, DragEvent event) {


                int dragAction = event.getAction();
                View dragView = (View) event.getLocalState();
                if (dragAction == DragEvent.ACTION_DRAG_EXITED) {
                } else if (dragAction == DragEvent.ACTION_DRAG_ENTERED) {
                } else if (dragAction == DragEvent.ACTION_DRAG_ENDED) {
                    if (dropEventNotHandled(event)) {
                        dragView.setVisibility(View.VISIBLE);
                    }
                } else if (dragAction == DragEvent.ACTION_DROP) {
                    //checkForValidMove((ChessBoardSquareLayoutView) view, dragView);

                    if (validMoves.contains(Math.abs(flipped - position))) {
                        boolean next = game.goForward();
                        Move move = game.getPosition().getLastMove();


                        if (move.getFromSqi() == fromSqi && move.getToSqi() == (Math.abs(flipped - position))) {
                            score += 10;
                            if (Util.getSetting("sound", activity.getApplicationContext()))
                                sp.play(soundIds[1], 1, 1, 1, 0, 1.0f);

                            activity.updateStats(true);
                            activity.updateScore("" + score);
//                            sqis.get(move.getToSqi()).setPiece(sqis.get(fromSqi).getPiece());
//                            sqis.get(move.getToSqi()).setColor(sqis.get(fromSqi).getColor());
//                            sqis.get(move.getFromSqi()).setPiece(0);

                            sqis.get(move.getToSqi()).setPiece(game.getPosition().getPiece(move.getToSqi()));
                            sqis.get(move.getToSqi()).setColor(game.getPosition().getColor(move.getToSqi()));

                            sqis.get(move.getFromSqi()).setPiece(game.getPosition().getPiece(move.getFromSqi()));
                            sqis.get(move.getFromSqi()).setColor(game.getPosition().getColor(move.getFromSqi()));

                            GridAdapter.this.notifyDataSetChanged();


                            if (game.getCurrentPly() < game.getNumOfPlies() && (Util.getSetting("multipleMoves", activity.getApplicationContext()))) {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        game.goForward();

                                        Move move;

                                        move = game.getLastMove();
//                                        sqis.get(move.getToSqi()).setPiece(sqis.get(move.getFromSqi()).getPiece());
//                                        sqis.get(move.getToSqi()).setColor(sqis.get(move.getFromSqi()).getColor());
//                                        sqis.get(move.getFromSqi()).setPiece(0);

                                        sqis.get(move.getToSqi()).setPiece(game.getPosition().getPiece(move.getToSqi()));
                                        sqis.get(move.getToSqi()).setColor(game.getPosition().getColor(move.getToSqi()));

                                        sqis.get(move.getFromSqi()).setPiece(game.getPosition().getPiece(move.getFromSqi()));
                                        sqis.get(move.getFromSqi()).setColor(game.getPosition().getColor(move.getFromSqi()));


                                        GridAdapter.this.notifyDataSetChanged();
                                    }
                                }, 500);
                            } else {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Util.getSetting("loadNext", activity.getApplicationContext())) {
                                            activity.getFragment().nextGame(score);
                                        } else {
                                            activity.getFragment().setReviewMode(true);

                                        }
                                    }
                                }, 100);

                            }


                            activity.getFragment().getHint().setVisibility(View.INVISIBLE);


                            return true;
                        } else {
                            game.goBack();
                            activity.getFragment().getHint().setVisibility(View.VISIBLE);

                            snackBar.setText("Valid Move, but think of a better solution");
                            snackBar.show();
                            score--;
                            activity.updateScore("" + score);
                        }
                    } else {
                        snackBar.setText("Invalid Move");
                        activity.getFragment().getHint().setVisibility(View.VISIBLE);
                        activity.updateStats(false);
                        snackBar.show();
                        score -= 1;
                        activity.updateScore("" + score);
                        if (Util.getSetting("sound", activity.getApplicationContext()))
                            sp.play(soundIds[0], 1, 1, 1, 0, 1.0f);

                        if (Util.getSetting("vibration", activity.getApplicationContext())) {
                            Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 500 milliseconds
                            vibrator.vibrate(500);
                        }

                    }

                    return false;

                }

                return true;


            }

            private boolean dropEventNotHandled(DragEvent dragEvent) {
                return !dragEvent.getResult();
            }
        });




        return convertView;


    }

    public void setPiece(Integer pieceId, int color, ImageView pieceView){
        pieceView.setVisibility(View.VISIBLE);
        if(pieceId == Chess.PAWN){
            pieceView.setImageResource(color==1?R.drawable.black_p2:R.drawable.white_p2);
        } else if(pieceId == Chess.KING){
            pieceView.setImageResource(color==1?R.drawable.black_k2:R.drawable.white_k2);

        } else if(pieceId == Chess.QUEEN){
            pieceView.setImageResource(color==1?R.drawable.black_q2:R.drawable.white_q2);

        } else if(pieceId == Chess.BISHOP){
            pieceView.setImageResource(color==1?R.drawable.black_b2:R.drawable.white_b2);

        } else if(pieceId == Chess.KNIGHT){
            pieceView.setImageResource(color==1?R.drawable.black_n2:R.drawable.white_n2);

        } else if(pieceId == Chess.ROOK){
            pieceView.setImageResource(color==1?R.drawable.black_r2:R.drawable.white_r2);

        } else {
            pieceView.setVisibility(View.INVISIBLE);
        }
    }

    private int highLightedSqi = -1;
    public void updateHint(){
        if(highLightedSqi == -1){
            game.goForward();
            int sqi = game.getPosition().getLastMove().getToSqi();
            sqis.get(sqi).setHighLighted(true);
            highLightedSqi = sqi;
            game.goBack();
        }
        else{
            sqis.get(highLightedSqi).setHighLighted(false);
            highLightedSqi = -1;
        }
        notifyDataSetChanged();
    }

    public void clearHing(){
        if(highLightedSqi!=-1){
            sqis.get(highLightedSqi).setHighLighted(false);
            highLightedSqi = -1;
        }
        notifyDataSetChanged();
    }

    public void stepBack(){
        Move move = game.getPosition().getLastMove();
        game.goBack();
        if(move == null) return;

        sqis.get(move.getToSqi()).setPiece(game.getPosition().getPiece(move.getToSqi()));
        sqis.get(move.getToSqi()).setColor(game.getPosition().getColor(move.getToSqi()));

        sqis.get(move.getFromSqi()).setPiece(game.getPosition().getPiece(move.getFromSqi()));
        sqis.get(move.getFromSqi()).setColor(game.getPosition().getColor(move.getFromSqi()));

        this.notifyDataSetChanged();

    }

    public void stepForward(){
        game.goForward();
        Move move = game.getPosition().getLastMove();
        if(move == null) return;

        sqis.get(move.getToSqi()).setPiece(game.getPosition().getPiece(move.getToSqi()));
        sqis.get(move.getToSqi()).setColor(game.getPosition().getColor(move.getToSqi()));

        sqis.get(move.getFromSqi()).setPiece(game.getPosition().getPiece(move.getFromSqi()));
        sqis.get(move.getFromSqi()).setColor(game.getPosition().getColor(move.getFromSqi()));

        this.notifyDataSetChanged();

    }

}