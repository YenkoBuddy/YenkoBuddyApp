package com.example.wendy.yenko;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class ViewReviews extends AppCompatActivity {

    List<AndroidVersion> reviewsList;
    ListView lv;
    public String passID;
    TextView labelHeading, textViewAverageRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reviews);

        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);
        String driverID = preferences.getString("driverID", "");
        String url = "http://sict-iis.nmmu.ac.za/yenko/app/view-ratings.php?driverID=" + driverID;

        lv= (ListView) findViewById(R.id.listView);
        labelHeading= (TextView) findViewById(R.id.labelHeading);
        textViewAverageRating= (TextView) findViewById(R.id.textViewAverageRating);



        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
             @Override
                    public void onResponse(String response) {
                        reviewsList = Response.parseData(response);
                        ReviewsAdapter adapter = new ReviewsAdapter(ViewReviews.this, reviewsList);
                        lv.setAdapter(adapter);
                 SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);
                 String driverName = preferences.getString("driverName", "");
                 String averageRating = preferences.getString("averageRating", "");
                 labelHeading.setText("Reviews Of " + driverName);
                 textViewAverageRating.setText(averageRating);

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                @Override
                 public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewReviews.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}
