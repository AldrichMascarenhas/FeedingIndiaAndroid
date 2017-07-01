package net.feedingindia.feedingindia.fragments;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import net.feedingindia.feedingindia.R;
import net.feedingindia.feedingindia.activites.MapsActivity;

import java.io.IOException;

/**
 * Created by Aldrich on 14-Oct-15.
 */
public class DefaultAddressFragment extends Fragment {




    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.defaultaddressfragment_layout, container, false);

        /*
        saveAddress = getActivity().getSharedPreferences(CheckDefaultAddress, Context.MODE_PRIVATE);
        editor = saveAddress.edit();
        */





        //New Address
        Button submit_new = (Button) view.findViewById(R.id.doDonate);
        submit_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MapsActivity)getActivity()).dispatchInformations("DefaultAddressFragment");




            }
        });

        return view;
    }



}