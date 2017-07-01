package net.feedingindia.feedingindia.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import net.feedingindia.feedingindia.R;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final String LOG_TAG = "RegisterActivity";

    private Toolbar mToolbar;


    public static final String SaveEmailData = "SaveEmailDataPreference";
    SharedPreferences saveEmail;
    SharedPreferences.Editor editor;


    //Code to Validate Emails
    //http://code.tutsplus.com/tutorials/creating-a-login-screen-using-textinputlayout--cms-24168
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    //Edit Texts and Validations
    //http://www.androidhive.info/2015/09/android-material-design-floating-labels-for-edittext/
    private TextInputLayout firstname_input_layout, lastname_input_layout, phonetext_input_layout, registrationemail_input_layout;


    private EditText FirstName, LastName, Phone, RegistrationEmail;

    String phoneNumberString, nameString, emailString;
    String stateString = "state";

    //List Strings to be Concatenated into Final Address
    List<String> slist = new ArrayList<String>();
    String ConcatenatedAddress = "address";

    //Media Type for Posting Data
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    CheckBox isCityAvailable;
    LinearLayout one;


    Button request_chapter, request_to_be_volunteer;
    Button finishRegistration;
    TextView selectedCity;
    //URL Encoding Link
    //http://stackoverflow.com/questions/10786042/java-url-encoding-of-query-string-parameters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        saveEmail = getSharedPreferences(SaveEmailData, Context.MODE_PRIVATE);
        editor = saveEmail.edit();


        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        one = (LinearLayout) findViewById(R.id.cityunavailable);
        request_chapter = (Button) findViewById(R.id.request_chapter);

        request_to_be_volunteer = (Button) findViewById(R.id.request_to_be_volunteer);
        selectedCity = (TextView)findViewById(R.id.selectedCity);


        Button pick_location = (Button) findViewById(R.id.longList);
        pick_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(RegisterActivity.this)
                        .title("Available Locations")
                        .items(R.array.locations_available)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                if(text.toString().equals("Other")) {

                                    selectedCity.setText("Selected Area : Others");

                                    finishRegistration.setVisibility(View.INVISIBLE);
                                    one.setVisibility(View.VISIBLE);

                                    request_chapter.setOnClickListener(new View.OnClickListener() {
                                                                           @Override
                                                                           public void onClick(View v) {

                                                                               sendEmail();
                                                                           }
                                                                       }
                                    );


                                    request_to_be_volunteer.setOnClickListener(new View.OnClickListener() {
                                                                                   @Override
                                                                                   public void onClick(View v) {

                                                                                       Log.d(LOG_TAG, "clicked button request_to_be_volunteer");


                                                                                       Intent i = new Intent(RegisterActivity.this, VolunteerRegisterActivity.class);
                                                                                       startActivity(i);


                                                                                   }
                                                                               }
                                    );

                                }else {
                                    one.setVisibility(View.INVISIBLE);
                                    finishRegistration.setVisibility(View.VISIBLE);

                                    stateString = text.toString();

                                    selectedCity.setText("Selected Area : " + stateString);
                                    //Toast.makeText(getApplicationContext(), "Selected Area : " + stateString, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .positiveText(android.R.string.cancel)
                        .show();
            }
        });

        firstname_input_layout = (TextInputLayout) findViewById(R.id.firstname_input_layout);
        lastname_input_layout = (TextInputLayout) findViewById(R.id.lastname_input_layout);
        phonetext_input_layout = (TextInputLayout) findViewById(R.id.phonetext_input_layout);
        registrationemail_input_layout = (TextInputLayout) findViewById(R.id.registrationemail_input_layout);


        FirstName = (EditText) findViewById(R.id.firstname_edit_text);
        LastName = (EditText) findViewById(R.id.lastname_edit_text);
        Phone = (EditText) findViewById(R.id.phone_edit_text);
        RegistrationEmail = (EditText)findViewById(R.id.registrationemail_edit_text);

        FirstName.addTextChangedListener(new MyTextWatcher(FirstName));
        LastName.addTextChangedListener(new MyTextWatcher(LastName));
        Phone.addTextChangedListener(new MyTextWatcher(Phone));


         finishRegistration = (Button) findViewById(R.id.finishRegistration);
        finishRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submitForm();
            }
        });
    }

    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {"hungerheroes.developer@gmail.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Start New Chapter");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi Feeding India,\n" +
                "\n" +
                "I would like you to start a chapter in my city soon, so I can donate food and help feed the needy too!\n" +
                "\n" +
                "My Name : \n" +
                "Contact Number : \n" +
                "City : \n" +
                "\n" +
                "Thanks!");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(RegisterActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    private void submitForm() {
        if (!validateSizeOfFirstName()) {
            return;
        }

        if (!validateSizeOfLastName()) {
            return;
        }

        if (!validatePhone()) {
            return;
        }
        if (!validateEmail()) {
            return;
        } else {


            phoneNumberString = Phone.getText().toString();
            try {
                phoneNumberString = URLEncoder.encode(phoneNumberString, "UTF-8");
            } catch (Exception e) {
                Log.d(LOG_TAG, "Failed Encoding!");
            }
            Log.d(LOG_TAG, "Phone : " + phoneNumberString);


            nameString = FirstName.getText().toString() + " " + LastName.getText().toString();
            try {
                nameString = URLEncoder.encode(nameString, "UTF-8");
            } catch (Exception e) {
                Log.d(LOG_TAG, "Failed Encoding!");
            }
            Log.d(LOG_TAG, "Name  : " + nameString);


            emailString = RegistrationEmail.getText().toString().toLowerCase();
            try {
                emailString = URLEncoder.encode(emailString, "UTF-8");
            } catch (Exception e) {
                Log.d(LOG_TAG, "Failed Encoding!");
            }
            Log.d(LOG_TAG, "Email : " + emailString);


            Snackbar.with(getApplicationContext()) // context
                    .text("Working....") // text to display
                    .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
                    .show(RegisterActivity.this); // activity where it is displayed


            RegisterAsyncTask task = new RegisterAsyncTask();
            task.execute();

        }


    }

    public class RegisterAsyncTask extends AsyncTask<Void, Void, Void> {
        String Response;
        String passedEmail;

        @Override
        protected Void doInBackground(Void... params) {

            try {

                String postbody = "";

                OkHttpClient client = new OkHttpClient();
                Request jsonrequest = new Request.Builder()
                        .url("https://feeding-india.herokuapp.com/signup/" + emailString + "?name=" + nameString + "&phone=" + phoneNumberString + "&state=" + stateString + "&address=" + ConcatenatedAddress)
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

            String accountExists = "already_exists";
            String InsufficientParameters = "insufficient_parameters";

            if (Response.equals(accountExists)) {
                Toast.makeText(getApplicationContext(), accountExists, Toast.LENGTH_SHORT).show();

            }
            if (Response.equals(InsufficientParameters)) {
                Toast.makeText(getApplicationContext(), InsufficientParameters, Toast.LENGTH_SHORT).show();

            } else {


                Snackbar.with(getApplicationContext()) // context
                        .text("Successfully Registered!") // text to display
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG) // make it shorter
                        .show(RegisterActivity.this); // activity where it is displayed


                editor.putString("Email", emailString);
                editor.putBoolean("isLoggedIn", true);
                editor.apply();


                Intent i = new Intent(getApplicationContext(), DonateActivity.class);
                startActivity(i);


            }

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
                case R.id.firstname_edit_text:
                    validateSizeOfFirstName();
                    break;
                case R.id.lastname_edit_text:
                    validateSizeOfLastName();
                    break;
                case R.id.phone_edit_text:
                    validatePhone();
                    break;
                case R.id.registrationemail_edit_text:
                    validateEmail();
                    break;

            }
        }
    }

    //Validations

    private boolean validateSizeOfFirstName() {
        if (FirstName.getText().toString().trim().isEmpty()) {
            firstname_input_layout.setError(getString(R.string.err_firstname));
            requestFocus(FirstName);
            return false;
        } else {
            firstname_input_layout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateSizeOfLastName() {
        if (LastName.getText().toString().trim().isEmpty()) {
            lastname_input_layout.setError(getString(R.string.err_lastname));
            requestFocus(LastName);
            return false;
        } else {
            lastname_input_layout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        if (Phone.getText().toString().trim().length() < 10) {
            phonetext_input_layout.setError(getString(R.string.err_phone));
            requestFocus(Phone);
            return false;
        } else {
            phonetext_input_layout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {

        String EmailCheck = registrationemail_input_layout.getEditText().getText().toString();
        matcher = pattern.matcher(EmailCheck);
        boolean flag = matcher.matches();

        if (!flag) {
            registrationemail_input_layout.setError(getString(R.string.err_email));
            requestFocus(RegistrationEmail);
            return false;
        } else {
            registrationemail_input_layout.setErrorEnabled(false);
        }

        return true;
    }


    //Focus on View
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
