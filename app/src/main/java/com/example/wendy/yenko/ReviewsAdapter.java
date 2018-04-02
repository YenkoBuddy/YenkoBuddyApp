package com.example.wendy.yenko;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by Wendy on 2017/09/12.
 */

public class ReviewsAdapter extends BaseAdapter {
    private Context context;
    private List<AndroidVersion> reviewsList;
    private LayoutInflater inflater = null;

    private RequestQueue queue;

    public ReviewsAdapter(Context context, List<AndroidVersion> list) {

        this.context = context;
        this.reviewsList = list;
        inflater = LayoutInflater.from(context);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        queue = Volley.newRequestQueue(context);
    }

    public class ViewHolder {

        TextView _journeyDate;
        TextView _comment;
        RatingBar _rating;
    }

    @Override
    public int getCount() {
        return reviewsList.size();
    }

    @Override
    public Object getItem(int position) {

        return reviewsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {


        final AndroidVersion reviews = reviewsList.get(position);
        final ViewHolder holder;
        if(convertView == null) {

            convertView = inflater.inflate(R.layout.list_item,null);
            holder = new ViewHolder();

            holder._journeyDate = (TextView) convertView.findViewById(R.id.textViewDate);
            holder._comment = (TextView) convertView.findViewById(R.id.textViewComment);
            holder._rating = (RatingBar) convertView.findViewById(R.id.reviewRatingBar);

            convertView.setTag(holder);
        }
        else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder._journeyDate.setText(reviews.getJourneyDate().toString());
        holder._comment.setText(reviews.getComment().toString());
        holder._rating.setRating(reviews.getRating());


        return convertView;
    }
}
