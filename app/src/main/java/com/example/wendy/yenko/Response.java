package com.example.wendy.yenko;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wendy on 2017/09/12.
 */

public class Response {
    static List<AndroidVersion> reviewsList;

    public static List<AndroidVersion> parseData(String content) {

        JSONArray reviews_arry = null;
        AndroidVersion reviews = null;
        try {

            reviews_arry = new JSONArray(content);
            reviewsList = new ArrayList<>();

            for (int i = 0; i < reviews_arry.length(); i++) {

                JSONObject obj = reviews_arry.getJSONObject(i);
                reviews = new AndroidVersion();

                reviews.setJourneyDate(obj.getString("journeyDate"));
                reviews.setComment(obj.getString("comment"));
                String rating = obj.getString("rating");
                if(rating == "null"){
                    reviews.setRating(0);
                }
                else{
                    reviews.setRating(Integer.parseInt(rating));
                }



                reviewsList.add(reviews);
            }
            return reviewsList;

        }
        catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
