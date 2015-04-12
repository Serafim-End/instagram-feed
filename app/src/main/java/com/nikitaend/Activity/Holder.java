package com.nikitaend.instafeed.Activity;

/**
 * @author Endaltsev Nikita
 *         start at 10.04.15.
 */
public class Holder {
    public String profileUrl;
    public String imageUrl;
    public String userName;
    public int countOfLikes;

    public Holder(String profileUrl, String imageUrl, String userName) {
        this.profileUrl = profileUrl;
        this.imageUrl = imageUrl;
        this.userName = userName;
    }

    public Holder(String profileUrl, String imageUrl, String userName, int countOfLikes) {
        this.profileUrl = profileUrl;
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.countOfLikes = countOfLikes;
    }
}