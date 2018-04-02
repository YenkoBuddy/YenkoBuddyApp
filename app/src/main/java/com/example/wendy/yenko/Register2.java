package com.example.wendy.yenko;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tooltip.Tooltip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register2 extends AppCompatActivity {

    //Variables to store data from previous activity ***********
    String userFirst, userLast, userName, userEmail, userPassword;
    //**********************************************************

    TextView txtView;
    Button reg_bn;
    EditText Name, Email, UserName, Password, Next1, Next2, Num1, Num2;
    String name, email, username, password, next1, num1, next2, num2;
    AlertDialog.Builder builder;
    String url = "http://sict-iis.nmmu.ac.za/yenko/app/register.php";
    ImageView tip;
    //String url = "http://10.102.133.120:80/demo/register.php";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityregister2);

        //*********session Receiver*********
        SharedPreferences preferences = getSharedPreferences("MYP", MODE_PRIVATE);
        String displayLog = preferences.getString("display", "");

        TextView txtView = (TextView) findViewById(R.id.textViewUser);
        txtView.setText(displayLog);
        //**********************************

        //Getting data from previous form************************
        userFirst = (getIntent().getStringExtra("NAME"));
        userLast = (getIntent().getStringExtra("LAST"));
        userEmail = (getIntent().getStringExtra("EMAIL"));
        userName = (getIntent().getStringExtra("USER"));
        userPassword = (getIntent().getStringExtra("PASSWORD"));
        //*******************************************************

        Next1 = (EditText) findViewById(R.id.regName1);
        Num1 = (EditText) findViewById(R.id.regNum1);
        Next2 = (EditText) findViewById(R.id.regName2);
        Num2 = (EditText) findViewById(R.id.regNum2);
        reg_bn = (Button) findViewById(R.id.btnFinishReg);
        tip = (ImageView)findViewById(R.id.tip);

        builder = new AlertDialog.Builder(Register2.this);
        tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.tip:
                        showTooltip(v, Gravity.BOTTOM);
                        break;
                }
            }
        });
        reg_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String Kin1Name = Next1.getText().toString();
                final String Kin1Num = Num1.getText().toString();
                final String Kin2Name = Next2.getText().toString();
                final String Kin2Num = Num2.getText().toString();

                if (Kin1Name.equals("") || Kin1Num.equals("") || Kin2Name.equals("") || Kin2Num.equals("")) {
                    builder.setTitle("Fields are empty");
                    builder.setMessage("Please fill in all fields");
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
                                        builder.setTitle("Yenko Buddy");
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

                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("FirstName", userFirst);
                            params.put("LastName", userLast);
                            params.put("EmailAddress", userEmail);
                            params.put("Username", userName);
                            params.put("Password", userPassword);
                            params.put("Kin1Name", Kin1Name);
                            params.put("Kin1ContactNumber", Kin1Num);
                            params.put("Kin2Name", Kin2Name);
                            params.put("Kin2ContactNumber", Kin2Num);

                            return params;
                        }
                    };
                    MySingleton.getInstance(Register2.this).addToRequestque(stringRequest);
                }
            }
        });
    }

    private void showTooltip(View v, int gravity) {
        ImageView imgV =(ImageView) v;
        Tooltip tooltip = new Tooltip.Builder(imgV)
                .setText("Details of those close to you, who will be notified via SMS when you have an emergency.\nClick tip to dismiss")
                .setTextColor(Color.BLACK)
                .setGravity(gravity)
                .setCornerRadius(8f)
                .setBackgroundColor(Color.LTGRAY)
                .setDismissOnClick(true)
                .show();
    }

    public void displayAlert(final String code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (code.equals("reg_success")) {
                    Intent intent = new Intent(Register2.this, Login.class);
                    startActivity(intent);
                    //finish();
                } else if (code.equals("reg_failed")) {
                    Next1.setText("");
                    Num1.setText("");
                    Next2.setText("");
                    Num2.setText("");
                    Intent intent = new Intent(Register2.this, Register1.class);
                    startActivity(intent);
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
