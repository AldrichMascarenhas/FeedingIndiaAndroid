package net.feedingindia.feedingindia.activites;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.datetimepicker.date.DatePickerDialog;
import com.appyvet.rangebar.RangeBar;
import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.cloudinary.Cloudinary;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import net.feedingindia.feedingindia.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DonateActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener
{

    private static final String LOG_TAG = "DonateActivity";

    private Toolbar mToolbar;
    private Calendar calendar;
    private DateFormat dateFormat;
    public static final String SaveEmailData = "SaveEmailDataPreference";


    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Feeding India";

    private Uri fileUri; // file url to store image/video

    ImageView img;
    RadioGroup FoodType, FoodPackaging;
    String FoodTypeString, FoodPackagingString, FoodDescriptionString, FoodImageString;
    String FoodDateAndTime = "test";
    int FoodCountInt;

    String time = "test";
    String date = "test";

    private EditText descriptionOfFood;
    private TextInputLayout descriptionOfFoodLayout;

    TextView textviewTime, textviewDate, foodForTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textviewTime = (TextView)findViewById(R.id.textview_time);
        textviewDate = (TextView)findViewById(R.id.textview_date);
        foodForTextView = (TextView)findViewById(R.id.food_for_textview_check);

        descriptionOfFoodLayout = (TextInputLayout) findViewById(R.id.description_input_layout);

        descriptionOfFood = (EditText) findViewById(R.id.description_edit_text);

        RangeBar rangebar = (RangeBar) findViewById(R.id.rangebar);

        // Sets the display values of the indices
        rangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {

                Log.d(LOG_TAG, "String : " + rightPinValue);
                FoodCountInt = Integer.parseInt(rightPinValue);

                foodForTextView.setText("I have food for : " + leftPinValue + " - " + rightPinValue + " People");
            }

        });





        img = (ImageView) findViewById(R.id.clickediamge);

        FoodType = (RadioGroup) findViewById(R.id.food_type_RadioGroup);

        FoodPackaging = (RadioGroup) findViewById(R.id.food_packaging_RadioGroup);

        FoodType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.check_if_Veg) {
                    FoodTypeString = "Veg";

                } else if (checkedId == R.id.check_if_NonVeg) {
                    FoodTypeString = "NonVeg";
                } else {
                    FoodTypeString = "BothVegAndNonVeg";
                }
            }

        });

        FoodPackaging.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.check_if_packed) {
                    FoodPackagingString = "Packaged";
                } else if (checkedId == R.id.check_if_notpacked) {
                    FoodPackagingString = "Unpackaged";
                } else {
                    FoodPackagingString = "BothPackagedAndUnpackaged";
                }
            }

        });


        FloatingActionButton btnCapturePicture = (FloatingActionButton) findViewById(R.id.btnCapturePicture);
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });


        Button finishDonation = (Button) findViewById(R.id.finishDonation);
        finishDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                doValidations();


            }
        });
        Button timeButton = (Button) findViewById(R.id.time_button);


        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        DonateActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                );
                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d("TimePicker", "Dialog was cancelled");

                    }
                });
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });

        Button dateButton = (Button)findViewById(R.id.date_button);
        dateButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                DatePickerDialog.newInstance(DonateActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");


            }
        });

    }

    private void doValidations() {
        if(!ifLoggedIn()){




            SnackbarManager.show(
                    Snackbar.with(getApplicationContext()) // context
                            .text("Login to Donate!") // text to display
                            .actionLabel("Click Here") // action button labe
                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)// l
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    Log.d(LOG_TAG, "gO TO Login");

                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                }
                            }) // action button's ActionClickListener
                    , this); // activity where it is displayed

            return;
        }

        if (!validateDescription()) {
            return;
        }
        if (!validatePickupDateAndTime()) {
            Snackbar.with(getApplicationContext()) // context
                    .text("Please select a Pickup Time") // text to display
                    .duration(Snackbar.SnackbarDuration.LENGTH_LONG) // make it shorter
                    .show(DonateActivity.this); // activity where it is displayed

            return;

        } else {


            FoodDescriptionString = descriptionOfFood.getText().toString();
            try {
                FoodDescriptionString = URLEncoder.encode(FoodDescriptionString, "UTF-8");
            } catch (Exception e) {
                Log.d(LOG_TAG, "Failed Encoding!");
            }


            try {
                FoodDateAndTime = URLEncoder.encode(FoodDateAndTime, "UTF-8");
            } catch (Exception e) {
                Log.d(LOG_TAG, "Failed Encoding!");
            }



            Snackbar.with(getApplicationContext()) // context
                    .text("Please Wait!") // text to display
                    .duration(Snackbar.SnackbarDuration.LENGTH_LONG) // make it shorter
                    .show(DonateActivity.this); // activity where it is displayed

            UploadImageAsyncTask task = new UploadImageAsyncTask();
            task.execute();

        }
    }

    private boolean ifLoggedIn(){
        boolean ifLoggedIn;

        SharedPreferences saveEmail = getSharedPreferences(SaveEmailData, Context.MODE_PRIVATE);
        ifLoggedIn = saveEmail.getBoolean("isLoggedIn", false);
        return ifLoggedIn;

    }

    private boolean imageNotClicked() {


        if (fileUri != null) {

            return true;
        } else {

            return false;
        }
    }

    private boolean validateDescription() {
        if (descriptionOfFood.getText().toString().trim().isEmpty()) {
            descriptionOfFoodLayout.setError(getString(R.string.err_description_food));
            requestFocus(descriptionOfFood);
            return false;
        } else {
            descriptionOfFoodLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePickupDateAndTime() {
        FoodDateAndTime = time + "_" + date;
        if (FoodDateAndTime.length()  <=10) {
            Log.d(LOG_TAG, "In function : " + FoodDateAndTime);

            return false;
        } else {
            return true;
        }
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }





    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_donate, menu);
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
    */

    /*
    * Capturing Camera Image will lauch camera app requrest image capture
    */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Log.d(LOG_TAG, "before getting file");

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        Log.d(LOG_TAG, "After getting file");


        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view

                previewCapturedImage();

                Snackbar.with(getApplicationContext()) // context
                        .text("Image Captured") // text to display
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
                        .show(DonateActivity.this); // activity where it is displayed

                Log.d(LOG_TAG, "File : " + fileUri);

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture


                Snackbar.with(getApplicationContext()) // context
                        .text("Image Capture Cancelled") // text to display
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
                        .show(DonateActivity.this); // activity where it is displayed

            } else {
                // failed to capture image


                Snackbar.with(getApplicationContext()) // context
                        .text("Image Capture Failed") // text to display
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
                        .show(DonateActivity.this); // activity where it is displayed
            }


        }
    }


    private void previewCapturedImage() {
        try {
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            img.setImageBitmap(bitmap);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public class UploadImageAsyncTask extends AsyncTask<Void, Void, Void> {
        String Response;
        Bitmap bitmap;
        boolean isClicked;

        @Override
        protected Void doInBackground(Void... params) {



            isClicked = imageNotClicked();
            if (isClicked){

                try {

                    Map config = new HashMap();
                    config.put("cloud_name", "dci6klnje");
                    config.put("api_key", "476794475422383");
                    config.put("api_secret", "GRIqQyPAjgQh4dl6NiWvEqfFlbk");
                    Cloudinary cloudinary = new Cloudinary(config);




                    InputStream is = new URL(fileUri.toString()).openStream();
                    bitmap = BitmapFactory.decodeStream(is);

                    bitmap = Bitmap.createScaledBitmap(bitmap, 800, 600, true);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                    InputStream in = new ByteArrayInputStream(bos.toByteArray());

                    Map result = cloudinary.uploader().upload(in, config);
                    FoodImageString = (String) result.get("secure_url");
                    Log.d(LOG_TAG, "URL : " + FoodImageString);
                    in.close();


                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Exceptions Happen", Toast.LENGTH_SHORT).show();
                }

            }else{

                //nothing

            }




            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            Log.d(LOG_TAG, "Final Data : ");

            Log.d(LOG_TAG, "Food_Description : " + FoodDescriptionString);
            Log.d(LOG_TAG, "Food_Count : " + FoodCountInt);
            Log.d(LOG_TAG, "Food_Image_String : " + FoodImageString);
            Log.d(LOG_TAG, "Food_Type : " + FoodTypeString);
            Log.d(LOG_TAG, "Food_Packaging : " + FoodPackagingString);
            Log.d(LOG_TAG, "Food_PickupDateAndTime : " + FoodDateAndTime);


            Intent intent = new Intent(getBaseContext(), MapsActivity.class);
            intent.putExtra("Food_Description", FoodDescriptionString);
            intent.putExtra("Food_Count", FoodCountInt);
            intent.putExtra("Food_Image_String", FoodImageString);
            intent.putExtra("Food_Type", FoodTypeString);
            intent.putExtra("Food_Packaging", FoodPackagingString);
            intent.putExtra("Food_PickupDateAndTime", FoodDateAndTime);

            startActivity(intent);


        }


    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String hourStringEnd = hourOfDayEnd < 10 ? "0" + hourOfDayEnd : "" + hourOfDayEnd;
        String minuteStringEnd = minuteEnd < 10 ? "0" + minuteEnd : "" + minuteEnd;
        time = hourString + "h" + minuteString + " " + hourStringEnd + "h" + minuteStringEnd;

        textviewTime.setText("Between " +hourString+ "h " + minuteString + "m " + " To " +  hourStringEnd + "h " + minuteStringEnd + "m");


        Log.d(LOG_TAG, time);

    }


    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        date = (dateFormat.format(calendar.getTime()));
        textviewDate.setText(dateFormat.format(calendar.getTime()));
    }



}
