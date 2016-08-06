package com.frostox.chessapp.models;

/**
 * Created by roger on 6/4/16.
 */
public class PGN {

    private String chapter;

    private String content;

    private String id;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }
}
