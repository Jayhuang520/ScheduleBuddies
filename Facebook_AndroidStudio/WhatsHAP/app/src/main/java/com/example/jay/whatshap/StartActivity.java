package com.example.jay.whatshap;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;

public class StartActivity extends AppCompatActivity {

    private final int DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mapIntent = new Intent(StartActivity.this, MapsActivity.class);
                Intent loginIntent = new Intent(StartActivity.this, MapsActivity.class);

                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if(accessToken != null){
                    StartActivity.this.startActivity(mapIntent);
                }
                else{
                    StartActivity.this.startActivity(loginIntent);
                }
            }
        }, DISPLAY_LENGTH);
    }

    @Override
    protected void onStart(){
        super.onStart();

    }
}
