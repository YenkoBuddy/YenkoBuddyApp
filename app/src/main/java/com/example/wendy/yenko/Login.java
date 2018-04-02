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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    //Defining views
    Button login_btn;
    EditText Username, Password;
    String username, password;
    TextView forgotPassword, creatAcc;

    AlertDialog.Builder builder;
    String url = "http://sict-iis.nmmu.ac.za/yenko/app/passenger-login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitylogin);

        //Getting id by their xml
        login_btn = (Button) findViewById(R.id.btn_login);
        Username = (EditText) findViewById(R.id.login_username);
        Password = (EditText) findViewById(R.id.login_password);

        forgotPassword = (TextView) findViewById(R.id.txtForgotPass);
        creatAcc = (TextView) findViewById(R.id.txtCreateAcc);

        //Next Activities
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPass = new Intent(Login.this, ForgotPassword.class);
                startActivity(forgotPass);
            }
        });

        creatAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent creatAcc = new Intent(Login.this, Register1.class);
                startActivity(creatAcc);
            }
        });


        builder = new AlertDialog.Builder(Login.this);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = Username.getText().toString();
                password = Password.getText().toString();


                if (username.equals("") || password.equals("")) {
                    builder.setTitle("Something Went Wrong...");
                    builder.setMessage("Please fill in all fields");
                    displayAlert("input_error");
                } else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String code = jsonObject.getString("code");
                                        String message = jsonObject.getString("message");

                                        if(code.equals("login_success")){
                                            String passengerID = jsonObject.getString("passengerID");
                                            String passengerName = jsonObject.getString("passengerName");
                                            createSessions(passengerID, passengerName);
                                        }
                                        builder.setTitle("Server Response");
                                        builder.setMessage(message);
                                        displayAlert(code);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("username", username);
                            params.put("password", password);

                            return params;
                        }
                    };
                    MySingleton.getInstance(Login.this).addToRequestque(stringRequest);

                }
            }
        });
    }

    public void createSessions(String passengerID, String passengerName) {
        //***************** Session *****************
        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        String passengerIDSession = preferences.getString(passengerID + "data", passengerID);
        String passengerNameSession = preferences.getString(passengerName + "data", passengerName);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("passengerID", passengerIDSession);
        editor.putString("passengerName", passengerNameSession);

        editor.commit();
        //*******************************************
    }

    public void displayAlert(final String code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code.equals("input_error")) {
                    Password.setText("");
                }

                else if (code.equals("login_failed")) {
                    Password.setText("");
                }
                else if (code.equals("login_success")) {
                    Password.setText("");
                    Username.setText("");
                    Intent scannerPage = new Intent(Login.this, Scanner.class);
                    startActivity(scannerPage);
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}