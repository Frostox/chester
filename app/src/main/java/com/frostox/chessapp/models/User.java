package com.frostox.chessapp.models;

import java.util.List;

/**
 * Created by roger on 6/4/16.
 */
public class User {
    private String uid;
    private String email;
    private Boolean blocked = false;

    private List<PGN> pgns;

    public List<PGN> getPgns() {
        return pgns;
    }

    public void setPgns(List<PGN> pgns) {
        this.pgns = pgns;
    }

    private String id;

    public void setId(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    class PGN {
        int score;

        int pgn;

        public int getPgn() {
            return pgn;
        }

        public void setPgn(int pgn) {
            this.pgn = pgn;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
