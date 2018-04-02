package com.example.wendy.yenko;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {
    Button bn_forgotPassword;
    EditText Email;
    TextView textView;
    String email;

    AlertDialog.Builder builder;
    String url = "http://sict-iis.nmmu.ac.za/yenko/app/reset-password.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        bn_forgotPassword = (Button) findViewById(R.id.button_reset_password);
        Email = (EditText) findViewById(R.id.email_reset_password);
        textView = (TextView) findViewById(R.id.textView);

        builder = new AlertDialog.Builder(ForgotPassword.this);
        bn_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = Email.getText().toString();

                if (email.equals("")) {
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Please fill all fields");
                    displayAlert("input_error");
                } else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new com.android.volley.Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String code = jsonObject.getString("code");
                                        String message = jsonObject.getString("message");
                                        builder.setTitle("Server Response");
                                        builder.setMessage(message);
                                        displayAlert(code);

                                        textView.setText(code);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("emailAddress", email);
                            return params;
                        }
                    };
                    MySingleton.getInstance(ForgotPassword.this).addToRequestque(stringRequest);

                }
            }
        });

    }

    public void displayAlert(final String code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code.equals("input_error")) {
                    Email.setText("");
                } else if (code.equals("reset_success")) {
                    Intent mainPage = new Intent(ForgotPassword.this, Login.class);
                    startActivity(mainPage);
                } else if (code.equals("reset_failed")) {

                    Email.setText("");
                }

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}