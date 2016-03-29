package com.example.asus_cp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.asus_cp.iweibo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by asus-cp on 2016-03-20.
 */
public class LogoActivity extends Activity {
    private String tag="LogoActivity";
        @Bind(R.id.img_logo)
        ImageView imgLogo;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.logo_layout);
            ButterKnife.bind(this);
            Animation animation= AnimationUtils.loadAnimation(this,R.anim.logo_alpha);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Intent intent=new Intent(LogoActivity.this,LoginActivity.class);
                    startActivity(intent);
                    Log.d(tag, "动画结束");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            imgLogo.startAnimation(animation);

        }

    }


