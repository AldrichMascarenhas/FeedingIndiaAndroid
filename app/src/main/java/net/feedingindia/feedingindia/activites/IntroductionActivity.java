package net.feedingindia.feedingindia.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;

import net.feedingindia.feedingindia.R;
import net.feedingindia.feedingindia.fragments.IntroductionFragment;

public class IntroductionActivity extends AppIntro {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(IntroductionFragment.newInstance(R.layout.intro_1));
        addSlide(IntroductionFragment.newInstance(R.layout.intro_2));
        addSlide(IntroductionFragment.newInstance(R.layout.intro_3));
        addSlide(IntroductionFragment.newInstance(R.layout.intro_4));


    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
        //Toast.makeText(getApplicationContext(), getString(R.string.skip), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    public void getStarted(View v){
        loadMainActivity();
    }
}
