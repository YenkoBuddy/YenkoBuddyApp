package com.example.wendy.yenko;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner extends AppCompatActivity implements View.OnClickListener {


    private RequestQueue queue;

    //view objects
    Button buttonScan;
    public String regNo, passengerID;
    String url = "http://sict-iis.nmmu.ac.za/yenko/app/fetch.php";

    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scanner, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            Intent logout = new Intent(Scanner.this, Login.class);
            startActivity(logout);
            return true;
        }
        else if (id == R.id.action_profile) {
            Intent profile = new Intent(Scanner.this, EditProfile.class);
            startActivity(profile);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        passengerID = preferences.getString("passengerID", "");

        queue = Volley.newRequestQueue(this);
        buttonScan = (Button)findViewById(R.id.buttonScanner);

        //initializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);
    }




    //Getting results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            //if qrcode has nothing in it
            if (result.getContents() == null)
            {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            }
            else
            {
                //if qrcode contains data
                try
                {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    regNo = obj.getString("regNo");

                    //session
                    SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

                    String regNoSession = preferences.getString(regNo + "data", regNo);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("regNo", regNoSession);
                    editor.putString("passengerID", passengerID);
                    editor.commit();
                    //mainpage
                    Intent mainPage = new Intent(Scanner.this, StartJourney.class);
                    startActivity(mainPage);

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }



    @Override
    public void onClick(View view)
    {
        //initiating the qr code scan
        qrScan.initiateScan();
    }

}