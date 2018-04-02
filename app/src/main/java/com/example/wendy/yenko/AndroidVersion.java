package com.example.wendy.yenko;

/**
 * Created by Wendy on 2017/09/12.
 */

class AndroidVersion {
    private String journeyDate;
    private String comment;
    private Integer rating;

    public Integer getRating() {
        return rating;
    }

    public Integer setRating(Integer rating) {
        this.rating = rating;
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(String journeyDate) {
        this.journeyDate = journeyDate;
    }
}
