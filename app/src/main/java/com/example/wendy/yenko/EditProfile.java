package com.example.wendy.yenko;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.example.wendy.yenko.R;
import com.tooltip.Tooltip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    String passengerID;
    TextView etFirstName,etLastName,etEmail,etkin1,etkin1Contact,etkin2,etkin2Contact, etPassword;
    ImageButton imageButtonHelp;
    Button buttonSave;
    String password, firstName, lastName, email, kin1, kin1Contact, kin2, kin2Contact;
    String url =  "http://sict-iis.nmmu.ac.za/yenko/app/passenger-details.php";
    String urlSave =  "http://sict-iis.nmmu.ac.za/yenko/app/update-details.php";
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        passengerID = preferences.getString("passengerID", "");

        etFirstName  = (EditText)findViewById(R.id.etFirstName);
        etLastName = (EditText)findViewById(R.id.etLastName);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etkin1  = (EditText)findViewById(R.id.etkin1);
        etkin1Contact = (EditText)findViewById(R.id.etkin1Contact);
        etkin2  = (EditText)findViewById(R.id.etkin2);
        etkin2Contact = (EditText)findViewById(R.id.etkin2Contact);
        etPassword = (EditText)findViewById(R.id.etPassword);

        imageButtonHelp = (ImageButton)findViewById(R.id.imageButtonHelp);
        buttonSave = (Button) findViewById(R.id.buttonSave);


        //change text size of textviews
        etFirstName.setTextSize(getResources().getDimension(R.dimen.textsize));
        etLastName.setTextSize(getResources().getDimension(R.dimen.textsize));
        etEmail.setTextSize(getResources().getDimension(R.dimen.textsize));
        etkin1.setTextSize(getResources().getDimension(R.dimen.textsize));
        etkin1Contact.setTextSize(getResources().getDimension(R.dimen.textsize));
        etkin2.setTextSize(getResources().getDimension(R.dimen.textsize));
        etkin2Contact.setTextSize(getResources().getDimension(R.dimen.textsize));
        etPassword.setTextSize(getResources().getDimension(R.dimen.textsize));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            etFirstName.setText(jsonObject.getString("firstName"));
                            etLastName.setText(jsonObject.getString("lastName"));
                            etEmail.setText(jsonObject.getString("emailAddress"));
                            etkin1.setText(jsonObject.getString("kin1Name"));
                            etkin1Contact.setText(jsonObject.getString("kin1Contact"));
                            etkin2.setText(jsonObject.getString("kin2Name"));
                            etkin2Contact.setText(jsonObject.getString("kin2Contact"));

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

                params.put("passengerID", passengerID);


                return params;
            }
        };
        MySingleton.getInstance(EditProfile.this).addToRequestque(stringRequest);

        imageButtonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imgV =(ImageView) view;
                Tooltip tooltip = new Tooltip.Builder(imgV)
                        .setText("To save changes, enter the password you use to login.\nClick tip to dismiss")
                        .setTextColor(Color.BLACK)
                        .setGravity(Gravity.TOP)
                        .setCornerRadius(8f)
                        .setBackgroundColor(Color.LTGRAY)
                        .setDismissOnClick(true)
                        .show();
            }
        });
        builder = new AlertDialog.Builder(EditProfile.this);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = etPassword.getText().toString();
                email = etEmail.getText().toString();
                kin1 = etkin1.getText().toString();
                kin1Contact = etkin1Contact.getText().toString();
                kin2 = etkin2.getText().toString();
                kin2Contact = etkin2Contact.getText().toString();


                if (email.equals("") || kin1.equals("") || kin1Contact.equals("") || kin2.equals("") || kin2Contact.equals("") ) {
                    builder.setTitle("Something Went Wrong...");
                    builder.setMessage("Please fill in all fields");
                    displayAlert("input_error");
                }
                else if(password.equals("")){
                    builder.setTitle("Something Went Wrong...");
                    builder.setMessage("Please fill in your password to save changes");
                    displayAlert("input_error");
                }
                else{
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlSave,
                        new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String code = jsonObject.getString("code");
                                    String message = jsonObject.getString("message");
                                    builder.setTitle("Update Details");
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

                        params.put("passengerID", passengerID);
                        params.put("password", password);
                        params.put("emailAddress", email);
                        params.put("kin1Name", kin1);
                        params.put("kin1ContactNumber", kin1Contact);
                        params.put("kin2Name", kin2);
                        params.put("kin2ContactNumber", kin2Contact);

                        return params;
                    }
                };
                MySingleton.getInstance(EditProfile.this).addToRequestque(stringRequest);
                }
            }
        });
    }

    private void displayAlert(final String code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code.equals("input_error")) {
                    etPassword.setText("");
                }

                else if (code.equals("update_failed")) {
                    etPassword.setText("");
                }
                else if (code.equals("update_success")) {
                    etPassword.setText("");
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}


