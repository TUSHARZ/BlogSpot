package com.example.tushar.menu;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.HttpAuthHandler;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {
ImageView image;
    Animation fade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

image=(ImageView)findViewById(R.id.imageView3);

fade= AnimationUtils.loadAnimation(this,R.anim.fadein);
        fade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(Splash.this,MainActivity.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        image.setAnimation(fade);
}}
