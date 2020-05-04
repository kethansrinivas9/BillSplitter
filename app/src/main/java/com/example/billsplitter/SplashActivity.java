package com.example.billsplitter;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//class for splash screen
public class SplashActivity extends AppCompatActivity {

    //initializing variables
    private ImageView logoImage;
    private TextView appTitle;
    private TextView subtitle;
    private static int splashTimeOut=5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appTitle = findViewById(R.id.appName);
        subtitle = findViewById(R.id.subtitle);
        logoImage = findViewById(R.id.splashImage);

        Typeface fontTitle = Typeface.createFromAsset(getAssets(),"fonts/Fredoka.ttf");
        Typeface fontSubtitle = Typeface.createFromAsset(getAssets(),"fonts/MontserratRegular.ttf");

        appTitle.setTypeface(fontTitle);
        subtitle.setTypeface(fontSubtitle);

        logoImage.setImageResource(R.drawable.bill_splitter_icon);
        //handler for splash activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        },splashTimeOut);

        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mysplashanimation);
        appTitle.startAnimation(myanim);
        logoImage.startAnimation(myanim);
        subtitle.startAnimation(myanim);
    }
}

