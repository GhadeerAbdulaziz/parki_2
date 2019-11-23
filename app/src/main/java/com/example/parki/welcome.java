package com.example.parki;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class welcome extends AppCompatActivity implements View.OnClickListener {
    private final int SPLASH_TIME_OUT = 3000;
    Button _btnlog2;

    MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        app = ((MyApplication) getApplication());


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcome.this.goToNextPage();
            }
        }, SPLASH_TIME_OUT);

        _btnlog2 = (Button) findViewById(R.id.btnlogw);
        _btnlog2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == _btnlog2) {
            goToNextPage();
        }
    }

    private void goToNextPage() {
        if (!app.isNetworkAvailable()) {
            Toast.makeText(welcome.this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
        if (app.currentUser == null) {

            Intent intent = new Intent(welcome.this, MainActivity.class);
            welcome.this.startActivity(intent);
            welcome.this.finish();
        } else {

            Intent intent = new Intent(welcome.this, MapsActivity.class);
            welcome.this.startActivity(intent);
            welcome.this.finish();

        }
    }

}

