package net.feedingindia.feedingindia.fragments;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import net.feedingindia.feedingindia.R;
import net.feedingindia.feedingindia.activites.MapsActivity;
import net.feedingindia.feedingindia.activites.VolunteerRegisterActivity;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aldrich on 14-Oct-15.
 */
public class NewAddressFragment extends Fragment {

    private static final String LOG_TAG = "NewAddressFragment";

    private TextInputLayout addressline1_input_layout, addressline2_input_layout, town_input_layout, state_input_layout, pincode_input_layout, landmark_input_layout;

    private EditText AddressLine1, AddressLine2, Town, State, Pincode, Landmark;

    //List Strings to be Concatenated into Final Address
    List<String> slist = new ArrayList<String>();
    String ConcatenatedAddress;


    public static final String CheckDefaultAddress = "AddressDataPreference";
    SharedPreferences saveAddress;
    SharedPreferences.Editor editor;
    String town;
    TextView selectedCity;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        saveAddress = getActivity().getSharedPreferences(CheckDefaultAddress, Context.MODE_PRIVATE);
        editor = saveAddress.edit();

        //Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.newaddressfragment_layout, container, false);


        addressline1_input_layout = (TextInputLayout) view.findViewById(R.id.addressline1_input_layout);
        addressline2_input_layout = (TextInputLayout) view.findViewById(R.id.addressline2_input_layout);
        // = (TextInputLayout) view.findViewById(R.id.town_input_layout);
        pincode_input_layout = (TextInputLayout) view.findViewById(R.id.pincode_input_layout);
        landmark_input_layout = (TextInputLayout) view.findViewById(R.id.landmark_input_layout);

        AddressLine1 = (EditText) view.findViewById(R.id.addressline1_edit_text);
        AddressLine2 = (EditText) view.findViewById(R.id.addressline2_edit_text);
        //Town = (EditText) view.findViewById(R.id.town_edit_text);
        Pincode = (EditText) view.findViewById(R.id.pincode_edit_text);
        Landmark = (EditText) view.findViewById(R.id.landmark_edit_text);

        AddressLine1.addTextChangedListener(new MyTextWatcher(AddressLine1));
        AddressLine2.addTextChangedListener(new MyTextWatcher(AddressLine2));
        //Town.addTextChangedListener(new MyTextWatcher(Town));
        Pincode.addTextChangedListener(new MyTextWatcher(Pincode));
        Landmark.addTextChangedListener(new MyTextWatcher(Landmark));

        selectedCity = (TextView)view.findViewById(R.id.selectedcityx);
        Button pick_location = (Button) view.findViewById(R.id.longList);
        pick_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title("Available Locations")
                        .items(R.array.locations_available)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {



                                    town = text.toString();

                                     selectedCity.setText("Selected Area : " + town);
                                    //Toast.makeText(getApplicationContext(), "Selected Area : " + stateString, Toast.LENGTH_LONG).show();

                            }
                        })
                        .positiveText(android.R.string.cancel)
                        .show();

            }
        });


        //New Address
        Button submit_new = (Button) view.findViewById(R.id.doDonate);
        submit_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitForm();


            }
        });

        return view;

    }


    private void submitForm() {

        if (!validateSizeOfAddressLine1()) {
            return;
        }
        if (!validateSizeOfAddressLine2()) {
            return;
        }

        if (!validateSizeOfPincode()) {
            return;
        }
        if (!validateSizeOfLandmark()) {
            return;
        }
        else{


            saveAddress = getActivity().getSharedPreferences(CheckDefaultAddress, Context.MODE_PRIVATE);
            editor = saveAddress.edit();



            slist.add(AddressLine1.getText().toString());
            slist.add(AddressLine2.getText().toString());
            slist.add(Pincode.getText().toString());
            slist.add(Landmark.getText().toString());
            slist.add(town);


            ConcatenatedAddress = TextUtils.join(";", slist);
            Log.d(LOG_TAG, "Before doing stuff : " + ConcatenatedAddress);

            try {
                ConcatenatedAddress = URLEncoder.encode(ConcatenatedAddress, "UTF-8");
            }catch (Exception e){
                Log.d(LOG_TAG, "Failed Encoding!");
            }
            Log.d(LOG_TAG, "Address : " + ConcatenatedAddress);



            editor.putBoolean("isAddressSaved", true);
            editor.putString("defaultAddress", ConcatenatedAddress);
            editor.apply();

            Toast.makeText(getActivity(), "Thank You!", Toast.LENGTH_SHORT).show();


            ((MapsActivity)getActivity()).dispatchInformations(ConcatenatedAddress);


        }




    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.addressline1_edit_text:
                    validateSizeOfAddressLine1();
                    break;
                case R.id.addressline2_edit_text:
                    validateSizeOfAddressLine2();
                    break;

                case R.id.pincode_edit_text:
                    validateSizeOfPincode();
                    break;
                case R.id.landmark_edit_text:
                    validateSizeOfLandmark();
                    break;
            }
        }
    }

    private boolean validateSizeOfAddressLine1() {
        if (AddressLine1.getText().toString().trim().isEmpty()) {
            addressline1_input_layout.setError(getString(R.string.err_addressline1));
            requestFocus(AddressLine1);
            return false;
        } else {
            addressline1_input_layout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateSizeOfAddressLine2() {
        if (AddressLine2.getText().toString().trim().isEmpty()) {
            addressline2_input_layout.setError(getString(R.string.err_addressline2));
            requestFocus(AddressLine2);
            return false;
        } else {
            addressline2_input_layout.setErrorEnabled(false);
        }

        return true;
    }



    private boolean validateSizeOfPincode() {
        if (Pincode.getText().toString().trim().isEmpty()) {
            pincode_input_layout.setError(getString(R.string.err_pincode));
            requestFocus(Pincode);
            return false;
        } else {
            pincode_input_layout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateSizeOfLandmark() {
        if (Landmark.getText().toString().trim().isEmpty()) {
            landmark_input_layout.setError(getString(R.string.err_landmark));
            requestFocus(Landmark);
            return false;
        } else {
            landmark_input_layout.setErrorEnabled(false);
        }

        return true;
    }

    //Focus on View
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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



}
