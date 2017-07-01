package net.feedingindia.feedingindia.activites;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;

import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import android.util.Log;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nispok.snackbar.Snackbar;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import net.feedingindia.feedingindia.R;
import net.feedingindia.feedingindia.fragments.DefaultAddressFragment;
import net.feedingindia.feedingindia.fragments.GeoLocationFragment;
import net.feedingindia.feedingindia.fragments.NewAddressFragment;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

public class MapsActivity extends FragmentActivity {

    private static final String LOG_TAG = "MapsActivity";


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    String myLocation = "error";
    RadioGroup LocationType;

    LinearLayout geo_location, new_address, default_llayout;

    //Media Type for Posting Data
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final String SaveEmailData = "SaveEmailDataPreference";

    String getEmail, Food_Description, Food_Image_String, Food_Type, Food_Packaging, Food_PickupDateAndTime;
    int Food_Count;


    private TextInputLayout addressline1_input_layout, addressline2_input_layout, town_input_layout, state_input_layout, pincode_input_layout, landmark_input_layout;

    private EditText AddressLine1, AddressLine2, Town, State, Pincode, Landmark;

    //List Strings to be Concatenated into Final Address
    List<String> slist = new ArrayList<String>();
    String ConcatenatedAddress;

    Fragment fr;
    FragmentManager fm;
    FragmentTransaction fragmentTransaction;

    RadioButton default_add, geo_addd, new_add;
    String DEFAULT_ADDRESS;


    public static final String CheckDefaultAddress = "AddressDataPreference";
    SharedPreferences saveAddress;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        SharedPreferences saveAddress = getSharedPreferences(CheckDefaultAddress, Context.MODE_PRIVATE);
        boolean isAddressSaved = saveAddress.getBoolean("isAddressSaved", false);

        if(isAddressSaved){
            Toast.makeText(getApplicationContext(), "Saved Address found", Toast.LENGTH_LONG).show();

            DEFAULT_ADDRESS = saveAddress.getString("defaultAddress", "default");

        }else {
            Toast.makeText(getApplicationContext(), "No saved Address, Please add new address", Toast.LENGTH_LONG).show();

        }


        default_add = (RadioButton) findViewById(R.id.default_location);
        geo_addd = (RadioButton) findViewById(R.id.geolocation);
        new_add = (RadioButton) findViewById(R.id.new_address);


        Intent intent = getIntent();

        SharedPreferences saveEmail = getSharedPreferences(SaveEmailData, Context.MODE_PRIVATE);
        getEmail = saveEmail.getString("Email", "");

        Log.d(LOG_TAG, getEmail);

        Food_Description = intent.getStringExtra("Food_Description");
        Food_Count = intent.getIntExtra("Food_Count", 0);
        Food_Image_String = intent.getStringExtra("Food_Image_String");
        Food_Type = intent.getStringExtra("Food_Type");
        Food_Packaging = intent.getStringExtra("Food_Packaging");
        Food_PickupDateAndTime = intent.getStringExtra("Food_PickupDateAndTime");

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Nothing
        } else {
            showGPSDisabledAlertToUser();
        }

        //Default Fragment:
        fr = new DefaultAddressFragment();
        fm = getFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_replacement, fr);
        fragmentTransaction.commit();

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting a reference to the map
        mMap = supportMapFragment.getMap();
        // Enabling MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);
        // Setting a click event handler for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                fr = new GeoLocationFragment();
                fm = getFragmentManager();
                fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_replacement, fr);
                fragmentTransaction.commit();


                geo_addd.setChecked(true);

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // Clears the previously touched position
                mMap.clear();

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);

                myLocation = latLng.latitude + " : " + latLng.longitude;



            }
        });


        LocationType = (RadioGroup) findViewById(R.id.location_type);

        LocationType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.geolocation) {

                    fr = new GeoLocationFragment();

                    fm = getFragmentManager();
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_replacement, fr);
                    fragmentTransaction.commit();


                } else if (checkedId == R.id.new_address) {

                    fr = new NewAddressFragment();

                    fm = getFragmentManager();
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_replacement, fr);
                    fragmentTransaction.commit();


                } else {
                    fr = new DefaultAddressFragment();

                    fm = getFragmentManager();
                    fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_replacement, fr);
                    fragmentTransaction.commit();

                }
            }

        });


    }


    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. \nWould you like to enable it?\nGPS is require to pinpoint your Location")
                .setCancelable(false)
                .setPositiveButton("Tap to Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    //List Of States
    private static final String[] COUNTRIES = new String[]{
            "Andaman and Nicobar Island ",
            "Andhra Pradesh",
            "Arunachal Pradesh",
            "Assam",
            "Bihar",
            "Chandigarh",
            "Chhattisgarh",
            "Dadra and Nagar Haveli",
            "Daman and Diu",
            "Delhi",
            "Goa",
            "Gujarat",
            "Haryana",
            "Himachal Pradesh",
            "Jammu and Kashmir",
            "Jharkhand",
            "Karnataka",
            "Kerala",
            "Lakshadweep",
            "Madhya Pradesh",
            "Maharashtra",
            "Manipur",
            "Meghalaya",
            "Mizoram",
            "Nagaland",
            "Odisha",
            "Puducherry",
            "Punjab",
            "Rajasthan",
            "Sikkim",
            "Tamil Nadu",
            "Telangana",
            "Tripura",
            "Uttarakhand",
            "Uttar Pradesh",
            "West Bengal"
    };

    public void dispatchInformations(String mesg){

        Snackbar.with(getApplicationContext()) // context
                .text("Completing Donation!") // text to display
                .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
                .show(MapsActivity.this); // activity where it is displayed


        String address;
        DonateAsyncTask task = new DonateAsyncTask();
        if(mesg.equals("DefaultAddressFragment")){
            address = DEFAULT_ADDRESS;
        }else if(mesg.equals("GeoLocationFragment")) {
            address = myLocation;

        }else{
            address = mesg;
        }
        task.sendParam(address);

        task.execute();
    }

    public class DonateAsyncTask extends AsyncTask<Void, Void, Void> {
        String Response;
        Bitmap bitmap;

        String passed_email;
        String Address;

        protected void sendParam(String s_URL) {
            Address = s_URL;
        }

        @Override
        protected Void doInBackground(Void... params) {


            try {

                passed_email = getEmail.toLowerCase();

                String postbody = "";

                OkHttpClient client = new OkHttpClient();
                Request jsonrequest = new Request.Builder()
                        .url("https://feeding-india.herokuapp.com/donate/" + passed_email + "?url=" + Food_Image_String + "&description=" + Food_Description
                                + "&time=" + Food_PickupDateAndTime + "&foodtype=" + Food_Type + "&packing=" + Food_Packaging + "&foodfor=" + Food_Count
                                + "&location=" + Address)

                        .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postbody))
                        .build();
                com.squareup.okhttp.Response responsejson = client.newCall(jsonrequest).execute();
                Response = responsejson.body().string();
                Log.e(LOG_TAG, "Respone JSON : " + Response);


            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Exceptions Happen", Toast.LENGTH_SHORT).show();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            String accountDoesNotExists = "does_not_exist";
            String InsufficientParameters = "insufficient_parameters";
            String Success = "success";

            if (Response.equals(accountDoesNotExists)) {

                Snackbar.with(getApplicationContext()) // context
                        .text("Account does not exist") // text to display
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG) // make it shorter
                        .show(MapsActivity.this); // activity where it is displayed


            } else if (Response.equals(InsufficientParameters)) {
                Snackbar.with(getApplicationContext()) // context
                        .text("Insufficient Parameters") // text to display
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG) // make it shorter
                        .show(MapsActivity.this); // activity where it is displayed


            } else {

                Snackbar.with(getApplicationContext()) // context
                        .text("Donation was successful!") // text to display
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG) // make it shorter
                        .show(MapsActivity.this); // activity where it is displayed


                Intent i = new Intent(getApplicationContext(), ThankYouScreen.class);
                startActivity(i);


            }


        }


    }

}
