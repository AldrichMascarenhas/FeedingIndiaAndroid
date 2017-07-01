package net.feedingindia.feedingindia.activites;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nispok.snackbar.Snackbar;

import net.feedingindia.feedingindia.R;

public class VolunteerRegisterActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_register);


        Snackbar.with(getApplicationContext()) // context
                .text("Loading Site. Please wait.") // text to display
                .duration(Snackbar.SnackbarDuration.LENGTH_LONG) // make it shorter
                .show(VolunteerRegisterActivity.this); // activity where it is displayed

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new MyWebViewClient());

        String url = "https://docs.google.com/forms/d/11PCMUXmCAPyNWbJyJ0OuJdq6ESJMemxtr3tkftSZl-k/viewform";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


}
