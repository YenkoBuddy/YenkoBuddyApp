package com.example.wendy.yenko;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RateReview extends AppCompatActivity {

    Button rate_review_bn;
    RatingBar Rating;
    EditText Review;
    Float rate;
    Integer rating;
    String review, problem, journeyID;
    AlertDialog.Builder builder;
    String url = "http://sict-iis.nmmu.ac.za/yenko/app/rate-review.php";

    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_review);

        queue = Volley.newRequestQueue(this);

        rate_review_bn = (Button)findViewById(R.id.btn_rate_review);
        Rating = (RatingBar)findViewById(R.id.journey_rating);
        Review = (EditText)findViewById(R.id.journey_review);
        builder = new AlertDialog.Builder(RateReview.this);
        rate_review_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = Rating.getRating();
                rating = Math.round(rate);
                review = Review.getText().toString();

                if(rating == 0 &&  review.equals(""))
                {
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Please fill at least one field");
                    displayAlert("input_error");
                }
                else
                {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new com.android.volley.Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String code = jsonObject.getString("code");
                                        String message = jsonObject.getString("message");

                                        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        //delete session values
                                        editor.remove("journeyID");
                                        editor.remove("driverID");
                                        editor.remove("driverName");
                                        editor.remove("taxiType");
                                        editor.commit();

                                        builder.setTitle("Server Response");
                                        builder.setMessage(message);
                                        displayAlert(code);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);
                            journeyID = preferences.getString("journeyID", "");

                            params.put("review", review);
                            params.put("rating", String.valueOf(rating));
                            params.put("journeyID", journeyID);
                            return params;
                        }
                    };
                    MySingleton.getInstance(RateReview.this).addToRequestque(stringRequest);

                }
            }
        });
    }

    public void displayAlert(final String code)
    {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(code.equals("review_success"))
                {
                    SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    //delete session values
                    editor.remove("journeyID");
                    editor.remove("driverID");
                    editor.remove("driverName");
                    editor.remove("taxiType");
                    editor.commit();

                    Intent home = new Intent(RateReview.this, Scanner.class);
                    startActivity(home);
                }

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }
}