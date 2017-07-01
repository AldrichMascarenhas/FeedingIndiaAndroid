package net.feedingindia.feedingindia.activites;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import net.feedingindia.feedingindia.R;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    public static final String FirstLaunchCheck = "FirstLaunchCheckPreference";
    public static final String SaveEmailData = "SaveEmailDataPreference";


    Boolean isInternetPresent;
    String email;

    //Code to Validate Emails
    //http://code.tutsplus.com/tutorials/creating-a-login-screen-using-textinputlayout--cms-24168
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    //Media Type for Posting Data
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    SharedPreferences saveEmail;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

         isInternetPresent = cd.isConnectingToInternet(); // true or false



        saveEmail = getSharedPreferences(SaveEmailData, Context.MODE_PRIVATE);
        editor = saveEmail.edit();

        //Check if App is launched for the first Time
        SharedPreferences sp = getSharedPreferences(FirstLaunchCheck, Context.MODE_PRIVATE);
        if (!sp.getBoolean("first", false)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("first", true);
            editor.commit();
            Intent intent = new Intent(this, IntroductionActivity.class);
            startActivity(intent);
        }

        final TextInputLayout emailTextInputLayout = (TextInputLayout) findViewById(R.id.emailtext_input_layout);


        //Button to handle Logins
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = emailTextInputLayout.getEditText().getText().toString();


                if (!validateEmail(email)) {
                    emailTextInputLayout.setError(getString(R.string.err_email));
                }else {



                    emailTextInputLayout.setErrorEnabled(false);

                    Log.d(LOG_TAG, email + " ");


                    if(isInternetPresent){

                        Snackbar.with(getApplicationContext()) // context
                                .text("Logging you in!") // text to display
                                .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
                                .show(MainActivity.this); // activity where it is displayed


                        LoginAsyncTask task = new LoginAsyncTask();
                        task.execute();
                    }
                    else{

                        Snackbar.with(getApplicationContext()) // context
                                .text("Please turn on Internet!") // text to display
                                .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
                                .show(MainActivity.this); // activity where it is displayed


                    }




                }


            }
        });

        //Button to handle Registration
        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent j = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(j);

            }
        });



        Button registerAsVolunteer = (Button) findViewById(R.id.registerAsVolunteer);
        registerAsVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent j = new Intent(getApplicationContext(), VolunteerRegisterActivity.class);
                startActivity(j);

            }
        });

        /*
        Button skipLogin = (Button) findViewById(R.id.skipLogin);
        skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                //Shared preference
                Intent j = new Intent(getApplicationContext(), DonateActivity.class);
                startActivity(j);

            }
        });
        */



    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public class LoginAsyncTask extends AsyncTask<Void, Void, Void> {
        String Response;
        String passedEmail;
        @Override
        protected Void doInBackground(Void... params) {

            try {
                passedEmail = email.toLowerCase();

                String postbody = "";

                OkHttpClient client = new OkHttpClient();
                Request jsonrequest = new Request.Builder()
                        .url("https://feeding-india.herokuapp.com/signin/" + passedEmail )
                        .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postbody))
                        .build();
                Response responsejson = client.newCall(jsonrequest).execute();
                Response = responsejson.body().string();
                Log.e(LOG_TAG, "Respone JSON : " + Response);

            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Exceptions Happen", Toast.LENGTH_SHORT).show();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            String accountDoesNotExist = "does_not_exist";
            if(Response.equals(accountDoesNotExist)){

                Snackbar.with(getApplicationContext()) // context
                        .text("Account does not exist. Please Register.") // text to display
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG) // make it shorter
                        .show(MainActivity.this); // activity where it is displayed

            }
            else{


                editor.putString("Email", email);
                editor.putBoolean("isLoggedIn", true);
                editor.apply();



                Snackbar.with(getApplicationContext()) // context
                        .text("Logged in!") // text to display
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
                        .show(MainActivity.this); // activity where it is displayed



                Intent i = new Intent(getApplicationContext(), DonateActivity.class);
                startActivity(i);


            }




        }


    }





















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on thethe Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
