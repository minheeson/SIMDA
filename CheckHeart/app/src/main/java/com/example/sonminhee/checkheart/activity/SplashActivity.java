package com.example.sonminhee.checkheart.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.sonminhee.checkheart.R;

/**
 * Created by sonminhee on 2017. 11. 30..
 */

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(new SplashHandler(), 3000);
    }

    private class SplashHandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), HeartCheckActivity.class));
            SplashActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
