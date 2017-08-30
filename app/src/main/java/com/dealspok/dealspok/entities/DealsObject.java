package com.dealspok.dealspok.entities;

public class DealsObject {

    private String dealCover;
    private String dealTitle;
    private String dealAuthor;

    public DealsObject(String songTitle, String songAuthor, String songCover) {
        this.dealCover = songCover;
        this.dealAuthor = songAuthor;
        this.dealTitle = songTitle;
    }

    public String getSongCover() {
        return dealCover;
    }

    public String getSongAuthor() {
        return dealAuthor;
    }

    public String getSongTitle() {
        return dealTitle;
    }
}
