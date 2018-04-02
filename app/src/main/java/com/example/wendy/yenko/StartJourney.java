package com.example.wendy.yenko;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StartJourney extends AppCompatActivity implements View.OnClickListener {

    private LocationManager locationManager;
    private LocationListener locationListener;
    ArrayList<String> mylist = new ArrayList<String>();
    ArrayList<String> listSelectedProblems = new ArrayList<String>();


    private Spinner spinner;
    private static final String urlProblems =  "http://sict-iis.nmmu.ac.za/yenko/app/journey-problems.php";
    protected List<DataObject> spinnerData;
    private RequestQueue queue;

    //view objects
    Button buttonScan, buttonStartJourney;
    ImageView imageholder;
    TextView textViewRegNo, textViewDriver, textViewType, textViewDescription, textViewAverage;
    TextView labelRegNo, labelDriver, labelType, labelDescription,labelAverage,labelViewReviews,labelSpinner,labelListView, textView;
    ListView listViewProblems;
    public String regNo, regNumber, code, passengerID, journeyID, driverID, problem;
    String url = "http://sict-iis.nmmu.ac.za/yenko/app/fetch.php";
    String journeyUrl = "http://sict-iis.nmmu.ac.za/yenko/app/journey.php";
    AlertDialog.Builder builder;
    private static final int SEND_SMS_PERMISSIONS_REQUEST = 1;
    String list = "", description ="";
    Map<String,String> myMap = new HashMap<String,String>();
    ArrayList < HashMap <String, String> > listProblems = new ArrayList<>();

    //qr code scanner object
    //private IntentIntegrator qrScan;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            Intent logout = new Intent(StartJourney.this, Login.class);
            startActivity(logout);
            return true;
        }
        else if (id == R.id.action_profile) {
            Intent profile = new Intent(StartJourney.this, EditProfile.class);
            startActivity(profile);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_journey);

        queue = Volley.newRequestQueue(this);
        requestJsonObject();

        //button = (Button) findViewById(R.id.buttonGetLocation);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //textView.append("\n" + location.getLongitude() + " " + location.getLatitude());

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        };

        //********************************************
        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        passengerID = preferences.getString("passengerID", "");
        regNo = preferences.getString("regNo", "");

        //passengerID = "41";
        //********************************************
        Intent intent=new Intent(getApplicationContext(),MainPage.class);
        final PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

        //view objects
        imageholder = (ImageView)findViewById(R.id.imageFromUrl);
        buttonStartJourney =(Button)findViewById(R.id.buttonStartJourney);
        textViewRegNo = (TextView)findViewById(R.id.textViewRegNo);
        textViewDriver = (TextView)findViewById(R.id.textViewDriver);
        textViewType = (TextView)findViewById(R.id.textViewType);
        textViewDescription = (TextView)findViewById(R.id.textViewDescription);
        textViewAverage = (TextView)findViewById(R.id.textViewRating);

        labelDescription = (TextView)findViewById(R.id.labelDescription);
        labelDriver = (TextView)findViewById(R.id.labelDriver);
        labelType = (TextView)findViewById(R.id.labelTaxiType);
        labelAverage = (TextView)findViewById(R.id.labelRating);
        labelRegNo = (TextView)findViewById(R.id.labelRegNo);
        labelViewReviews = (TextView)findViewById(R.id.labelViewReviews);
        labelListView = (TextView)findViewById(R.id.labelListView);
        labelSpinner =  (TextView)findViewById(R.id.labelSpinner);
        listViewProblems =  (ListView)findViewById(R.id.listViewProblems);

        labelViewReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewReviews = new Intent(StartJourney.this, ViewReviews.class);
                startActivity(viewReviews);
            }
        });

        //initializing scan object
        //qrScan = new IntentIntegrator(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            textViewDriver.setText(jsonObject.getString("driverName"));
                            textViewType.setText(jsonObject.getString("type"));
                            textViewDescription.setText(jsonObject.getString("description"));
                            textViewAverage.setText(jsonObject.getString("rating"));

                            String foundDriverID = jsonObject.getString("driverID");
                            String driverName = jsonObject.getString("driverName");
                            String taxiDesc = jsonObject.getString("description");

                            String photo = jsonObject.getString("photo");
                            String imageUrl = "http://sict-iis.nmmu.ac.za/yenko/yenko3/driver-images" + photo;
                            Picasso.with(getApplicationContext()).load(imageUrl).into(imageholder);
                            createDriverSession(foundDriverID, driverName, taxiDesc);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("regNo", regNo);

                return params;
            }
        };
        MySingleton.getInstance(StartJourney.this).addToRequestque(stringRequest);
        textViewRegNo.setText(regNo);

        buttonStartJourney.setVisibility(View.VISIBLE);
        labelDescription.setVisibility(View.VISIBLE);
        labelDriver.setVisibility(View.VISIBLE);
        labelRegNo.setVisibility(View.VISIBLE);
        labelType.setVisibility(View.VISIBLE);
        labelAverage.setVisibility(View.VISIBLE);
        textViewDescription.setVisibility(View.VISIBLE);
        textViewDriver.setVisibility(View.VISIBLE);
        textViewRegNo.setVisibility(View.VISIBLE);
        textViewType.setVisibility(View.VISIBLE);
        textViewAverage.setVisibility(View.VISIBLE);
        imageholder.setVisibility(View.VISIBLE);

        //attaching onclick listener
        buttonStartJourney.setOnClickListener(this);
        builder = new android.app.AlertDialog.Builder(StartJourney.this);

        if (ContextCompat.checkSelfPermission(StartJourney.this,
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(StartJourney.this,
                    android.Manifest.permission.SEND_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(StartJourney.this,
                        new String[]{android.Manifest.permission.SEND_SMS},
                        SEND_SMS_PERMISSIONS_REQUEST);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        buttonStartJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, journeyUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String journeyID = jsonObject.getString("journeyID");
                                    createJourneySession(journeyID);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();

                        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);


                        driverID = preferences.getString("driverID", "");

                        params.put("regNo", regNo);
                        params.put("passenger", passengerID);
                        params.put("driverID", driverID);

                        return params;
                    }
                };
                MySingleton.getInstance(StartJourney.this).addToRequestque(stringRequest);
                buttonStartJourney.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.VISIBLE);
                labelViewReviews.setVisibility(View.VISIBLE);
                labelListView.setVisibility(View.VISIBLE);
                labelSpinner.setVisibility(View.VISIBLE);
                listViewProblems.setVisibility(View.VISIBLE);

            }
        });

        configure_button();
    }

    private void panicAlert(final String code, final String kin1Name, final String kin2Name, final String kin1ContactNumber, final String kin2ContactNumber) {
        builder.setPositiveButton("In Danger", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code.equals("panic")) {
                    //TO get the location,manifest file is added with 2 permissions
                    //Location Manager is used to figure out which location provider needs to be used.
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


                    //Best location provider is decided by the criteria
                    Criteria criteria = new Criteria();
                    //location manager will take the best location from the criteria
                    locationManager.getBestProvider(criteria, true);

                    //nce  you  know  the  name  of  the  LocationProvider,  you  can  call getLastKnownPosition() to  find  out  where  you  were  recently.
                    if (ActivityCompat.checkSelfPermission(StartJourney.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(StartJourney.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                                    ,10);
                        }
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));


                    // city = String.valueOf(location.getExtras());
                    Log.d("Tag","1");
                    List<Address> addresses;

                    try {
                        Geocoder gcd=new Geocoder(getBaseContext(), Locale.getDefault());

                        addresses=gcd.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        if(addresses.size()>0)

                        {


                            String city = addresses.get(0).getLocality().toString();
                            String suburb = addresses.get(0).getSubLocality().toString();
                            String place = addresses.get(0).getFeatureName().toString();
                            String loc = place + " " + suburb + " " + city;

                            sendSMS(kin1Name, kin2Name,kin1ContactNumber, kin2ContactNumber, place, city, suburb );




                        }

                    } catch (IOException e) {
                        e.printStackTrace();

                    }

//                    Intent intent = new Intent(getApplicationContext(), MainPage.class);
//                    final PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//                    SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);
//                    SmsManager sms=SmsManager.getDefault();
//                    sms.sendTextMessage("0836925761", null, "Yenko Testing", pi,null);
                }

            }
        });
        builder.setNegativeButton("Mistake", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);



            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sendSMS(String kin1Name, String kin2Name, String kin1ContactNumber, String kin2ContactNumber, String place, String city, String suburb) {
        Intent intent = new Intent(getApplicationContext(), MainPage.class);
        final PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        String passengerName = preferences.getString("passengerName", "");
        String driverName = preferences.getString("driverName", "");
        String taxiDesc = preferences.getString("taxiDesc", "");

        String kin1Message1 = "Hi " + kin1Name + ", " + " it's " + passengerName + " I have an emergency in a taxi, please help.\nTaxi Details:\nRegistration No: " + regNo;
        String kin1Message2 = "Taxi Description: " + taxiDesc+ "\nDriver : " + driverName + "\nLocation: " + place +"\n" + suburb + "\n" + city;
        String kin2Message1 = "Hi " + kin2Name + ", " + " it's " + passengerName + " I have an emergency in a taxi, please help.\nTaxi Details:\nRegistration No: " + regNo;
        String kin2Message2 = "Taxi Description: " + taxiDesc+ "\nDriver : " + driverName + "\nLocation: " + place +"\n" + suburb + "\n" + city;
        SmsManager sms=SmsManager.getDefault();
        sms.sendTextMessage(kin1ContactNumber, null, kin1Message1, pi,null);
        sms.sendTextMessage(kin1ContactNumber, null, kin1Message2, pi,null);
        sms.sendTextMessage(kin2ContactNumber, null, kin2Message1, pi,null);
        sms.sendTextMessage(kin2ContactNumber, null, kin2Message2, pi,null);

        String sentCode = "alert_sent";
        String theMessage = kin1Name +" , " + kin2Name + " and the corrective Taxi Association have been sent your location,driver and taxi details.";
        builder.setTitle("Notification Sent");
        builder.setMessage(theMessage);
        sentAlert(sentCode);




    }


    private void sentAlert(final String sentCode) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (sentCode.equals("alert_sent")) {
                }

            }
        });
        builder.setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void requestJsonObject() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlProblems, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder builder = new GsonBuilder();
                Gson mGson = builder.create();
                spinnerData = Arrays.asList(mGson.fromJson(response, DataObject[].class));
                //display first question to the user
                if(null != spinnerData){
                    spinner = (Spinner) findViewById(R.id.spinner3);
                    spinner.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {
                                public void onItemSelected(

                                        AdapterView<?> parent, View view, int position, long problemID) {
                                    DataObject selected = (DataObject) parent.getItemAtPosition(position);
                                    problem = selected.getProblemID();
                                    String selectedDescription = selected.getName();
                                    description  = description + selectedDescription + "\n";

                                    mylist.add(problem);
                                    myMap.put("problemID", problem);

                                    String [] listSelectedProblems = {description};

                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(StartJourney.this,
                                            android.R.layout.simple_list_item_1, listSelectedProblems){
                                        @Override
                                        public View getView(int position, View convertView, ViewGroup parent){
                                            /// Get the Item from ListView
                                            View view = super.getView(position, convertView, parent);

                                            TextView tv = (TextView) view.findViewById(android.R.id.text1);

                                            // Set the text size for ListView each item
                                            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);

                                            // Return the view
                                            return view;
                                        }
                                    };
                                    listViewProblems.setAdapter(arrayAdapter);


                                }

                                public void onNothingSelected(AdapterView<?> parent) {
                                    //selectedOption.setText("Spinner1: unselected");
                                }
                            });

                    assert spinner != null;
                    SpinnerAdapter spinnerAdapter = new SpinnerAdapter(StartJourney.this, spinnerData);
                    spinner.setAdapter(spinnerAdapter);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }


    void configure_button()
    {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }

    }

    public void displayAlert(final String code) {
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (code.equals("journey_success")) {
                    Intent rateR = new Intent(StartJourney.this, RateReview.class);
                    startActivity(rateR);
                }

            }
        });
        builder.setNegativeButton("Not Now Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                //delete session values
                editor.remove("journeyID");
                editor.remove("driverID");
                editor.remove("driverName");
                editor.remove("taxiType");
                editor.commit();
                textViewRegNo.setText("");
                textViewDescription.setText("");
                textViewDriver.setText("");
                textViewType.setText("");
                String imageUrl = "http://sict-iis.nmmu.ac.za/yenko/yenko3/driver-images/jpg";
                Picasso.with(getApplicationContext()).load(imageUrl).into(imageholder);

                Intent home = new Intent(StartJourney.this, Scanner.class);
                startActivity(home);

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void createJourneySession(String journeyID) {
        //***************** Session *****************
        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        String journeyIDSession = preferences.getString(journeyID + "data", journeyID);
        SharedPreferences.Editor editor = preferences.edit();
        //editor.putString(username + "data", username);
        editor.putString("journeyID", journeyIDSession);
        editor.commit();
        Intent main = new Intent(StartJourney.this, MainPage.class);
        startActivity(main);
        //*******************************************
    }




    private void createDriverSession(String foundDriverID, String driverName, String taxiDesc) {
        //***************** Session *****************
        SharedPreferences preferences = getSharedPreferences("MYPREFS", MODE_PRIVATE);

        String driverIDSession = preferences.getString(foundDriverID + "data", foundDriverID);
        String driverNameSession = preferences.getString(foundDriverID + "data", driverName);
        String taxiDescSession = preferences.getString(foundDriverID + "data", taxiDesc);

        SharedPreferences.Editor editor = preferences.edit();
        //editor.putString(username + "data", username);
        //textViewJourneyID.setText(driverID);

        editor.putString("driverID", driverIDSession);
        editor.putString("driverName", driverNameSession);
        editor.putString("taxiDesc", taxiDescSession);
        editor.commit();
        //driverID = preferences.getString("driverID", "");

        //*******************************************
    }

    @Override
    public void onClick(View view)
    {
        //initiating the qr code scan
        //qrScan.initiateScan();
    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case SEND_SMS_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
        //For the GPS button
        switch (requestCode)
        {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }
}