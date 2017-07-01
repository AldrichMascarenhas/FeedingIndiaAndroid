package net.feedingindia.feedingindia.activites;

import android.app.Application;

import com.pushbots.push.Pushbots;

/**
 * Created by Aldrich on 21-Oct-15.
 */

public class AppApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        Pushbots.sharedInstance().init(this);



        // Initialize the singletons so their instances
        // are bound to the application process.
        initSingletons();
    }

    protected void initSingletons()
    {
        // Initialize the instance of MySingleton
    }


}