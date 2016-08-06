package com.frostox.chessapp.wrappers;

/**
 * Created by roger on 13/4/16.
 */
public class SQIWrapper {
    private Integer piece;
    private int sqi;
    private boolean isBlack;
    private int color;

    private boolean highLighted = false;

    public boolean isHighLighted() {
        return highLighted;
    }

    public void setHighLighted(boolean highLighted) {
        this.highLighted = highLighted;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public SQIWrapper(int sqi){
        this.sqi = sqi;
        isBlack = !((sqi / 8) % 2 == sqi % 2);
        if((((float) sqi) % 8.0f == 0.0f)) isBlack = !isBlack();
    }

    public Integer getPiece() {
        return piece;
    }

    public void setPiece(Integer piece) {
        this.piece = piece;
    }

    public int getSqi() {
        return sqi;
    }

    public void setSqi(int sqi) {
        this.sqi = sqi;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void setIsBlack(boolean isBlack) {
        this.isBlack = isBlack;
    }
}
